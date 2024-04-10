package com.ing.assignment.orderprocessor.engine;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.dto.PlaceOrder;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.orderprocessor.model.Inventory;
import com.ing.assignment.orderprocessor.repository.InventoryRepository;
import com.ing.assignment.orderprocessor.utils.CarOrderFeedbackProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

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
        }
    }

    private boolean inventoryAvailable(Integer required) {
        Inventory inventory = inventoryRepository.findOneByType(VehicleType.CAR);
        return inventory.getQuantity() >= required;
    }

    private void processOrder(PlaceOrder order) {
        try {
            Thread.sleep(getRandomWaitTime());
            Inventory inventory = inventoryRepository.findOneByType(VehicleType.CAR);
            inventory.setQuantity(inventory.getQuantity()-order.getQuantity());
            inventoryRepository.save(inventory);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted");
        }
        System.out.println("Processing record: " + order.getOrderId());
    }

    public static Long getRandomWaitTime() {
        return (long) (RANDOM.nextInt((6)) + 5) * 1000;
    }
}
