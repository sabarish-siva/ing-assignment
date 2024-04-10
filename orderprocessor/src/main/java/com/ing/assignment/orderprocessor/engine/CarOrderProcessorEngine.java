package com.ing.assignment.orderprocessor.engine;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.dto.PlaceOrder;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.orderprocessor.model.InventoryDetail;
import com.ing.assignment.orderprocessor.repository.InventoryRepository;
import com.ing.assignment.orderprocessor.utils.CarOrderFeedbackProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Core class to process car orders. Extends the {@link AbstractScheduledKafkaConsumer} to fetch
 * periodically from the <b>process-car-orders-topic</b> kafka topic. Processes the orders and commits
 * if there is enough inventory. Sends initial feedback at the start of processing and final feedback
 * after finishing the order processing to the kafka topic <b>car-order-feedback-topic</b>.
 * {@link ConsumerFactory} bean can be found at {@link com.ing.assignment.orderprocessor.config.KafkaConfig} file.
 */
@Component
public class CarOrderProcessorEngine extends AbstractScheduledKafkaConsumer<Object> {

    private final CarOrderFeedbackProducer carOrderFeedbackProducer;
    private final InventoryRepository inventoryRepository;
    private static final Random RANDOM = new Random();

    public CarOrderProcessorEngine(@Qualifier("carConsumerFactory") ConsumerFactory<String, Object> consumerFactory,
                                   CarOrderFeedbackProducer carOrderFeedbackProducer, InventoryRepository inventoryRepository) {
        super(consumerFactory);
        this.carOrderFeedbackProducer = carOrderFeedbackProducer;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    protected String getTaskName() {
        return "Processing Car Orders";
    }

    @Override
    protected String getTopicName() {
        return "process-car-orders-topic";
    }

    @Override
    protected void processMessages() {
        ConsumerRecords<String, Object> records = pollRecords();
        for (ConsumerRecord<String, Object> record : records) {
            processRecord(record);
        }
    }

    private void processRecord(ConsumerRecord<String, Object> record) {
        PlaceOrder order = (PlaceOrder) record.value();

        if(inventoryAvailable(order.getQuantity())) {
            OrderFeedback feedback = new OrderFeedback();
            feedback.setOrderId(order.getOrderId());
            feedback.setStatus(OrderStatus.PROCESSING);

            carOrderFeedbackProducer.sendFeedback(feedback);
            processOrder(order);

            feedback.setStatus(OrderStatus.FINISHED);
            carOrderFeedbackProducer.sendFeedback(feedback);

            commitOffsets();
        } else {
            /*for some reason, the polling doesn't continue to fetch from same offset even
            though the auto commit is off and we dont manually commit if inventory check fails.
            A server restart however makes the consumption from stopped offset. So I persume there
            is some kind of soft commit or something of that sort (which I dont know for sure).
            Hence, using a workaround for now to maintain offset when the record is not processed.
             */
            kafkaConsumer.seek(new TopicPartition(record.topic(),record.partition()),record.offset());
        }
    }

    private boolean inventoryAvailable(Integer required) {
        InventoryDetail inventoryDetail = inventoryRepository.findOneByType(VehicleType.CAR);
        return inventoryDetail != null && inventoryDetail.getQuantity() >= required;
    }

    private void processOrder(PlaceOrder order) {
        try {
            Thread.sleep(getRandomWaitTime());
            InventoryDetail inventoryDetail = inventoryRepository.findOneByType(VehicleType.CAR);
            inventoryDetail.setQuantity(inventoryDetail.getQuantity()-order.getQuantity());
            inventoryRepository.save(inventoryDetail);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted");
        }
        System.out.println("Processing record: " + order.getOrderId());
    }

    public static Long getRandomWaitTime() {
        return (long) (RANDOM.nextInt((6)) + 5) * 1000;
    }
}
