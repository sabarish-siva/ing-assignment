package com.ing.assignment.orderprocessor.engine;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.dto.PlaceOrder;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.orderprocessor.model.InventoryDetail;
import com.ing.assignment.orderprocessor.repository.InventoryRepository;
import com.ing.assignment.orderprocessor.utils.TruckOrderFeedbackProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Core class to process truck orders. Extends the {@link AbstractScheduledKafkaConsumer} to fetch
 * periodically from the <b>process-truck-orders-topic</b> kafka topic. Processes the orders and commits
 * if there is enough inventory. Sends initial feedback at the start of processing and final feedback
 * after finishing the order processing to the kafka topic <b>truck-order-feedback-topic</b>.
 * {@link ConsumerFactory} bean can be found at {@link com.ing.assignment.orderprocessor.config.KafkaConfig} file.
 */
@Component
public class TruckOrderProcessorEngine extends AbstractScheduledKafkaConsumer<Object> {

    private final TruckOrderFeedbackProducer truckOrderFeedbackProducer;
    private final InventoryRepository inventoryRepository;
    private static final Random RANDOM = new Random();

    public TruckOrderProcessorEngine(@Qualifier("truckConsumerFactory") ConsumerFactory<String, Object> consumerFactory,
                                   TruckOrderFeedbackProducer truckOrderFeedbackProducer, InventoryRepository inventoryRepository) {
        super(consumerFactory);
        this.truckOrderFeedbackProducer = truckOrderFeedbackProducer;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    protected String getTaskName() {
        return "Processing Truck Orders";
    }

    @Override
    protected String getTopicName() {
        return "process-truck-orders-topic";
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

            truckOrderFeedbackProducer.sendFeedback(feedback);
            processOrder(order);

            feedback.setStatus(OrderStatus.FINISHED);
            truckOrderFeedbackProducer.sendFeedback(feedback);

            commitOffsets();
        } else {
            kafkaConsumer.seek(new TopicPartition(record.topic(),record.partition()),record.offset());
        }
    }

    private boolean inventoryAvailable(Integer required) {
        InventoryDetail inventoryDetail = inventoryRepository.findOneByType(VehicleType.TRUCK);
        return inventoryDetail != null && inventoryDetail.getQuantity() >= required;
    }

    private void processOrder(PlaceOrder order) {
        try {
            Thread.sleep(getRandomWaitTime());
            InventoryDetail inventoryDetail = inventoryRepository.findOneByType(VehicleType.TRUCK);
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
