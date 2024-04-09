package com.ing.assignment.orderprocessor.engine;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.orderprocessor.utils.CarOrderFeedbackProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Component
public class CarOrderProcessorEngine extends AbstractScheduledKafkaConsumer<UUID> {

    @Value("${kafka.topic.process-car-orders}")
    private String carOrderProcessTopic;

    private final CarOrderFeedbackProducer carOrderFeedbackProducer;
    private static final Random RANDOM = new Random();

    public CarOrderProcessorEngine(@Qualifier("carConsumerFactory") ConsumerFactory<String, UUID> consumerFactory,
                                   CarOrderFeedbackProducer carOrderFeedbackProducer) {
        super(consumerFactory);
        this.carOrderFeedbackProducer = carOrderFeedbackProducer;
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
        ConsumerRecords<String, UUID> records = pollRecords(Optional.empty());
        for (ConsumerRecord<String, UUID> record : records) {
            processRecord(record);
        }
    }

    private void processRecord(ConsumerRecord<String, UUID> record) {
        UUID message = record.value();
        boolean isValid = validateRecord(String.valueOf(message));

        if(isValid) {
            OrderFeedback feedback = new OrderFeedback();
            feedback.setOrderId(message);
            feedback.setStatus(OrderStatus.PROCESSING);

            // Send initial feedback
            carOrderFeedbackProducer.sendFeedback(feedback);
            processOrder(message);

            feedback.setStatus(OrderStatus.FINISHED);
            // Send feedback again after processing
            carOrderFeedbackProducer.sendFeedback(feedback);

            commitOffsets();
        }
    }

    private boolean validateRecord(String message) {
        // Implement your validation logic here
        return true; // For demonstration purposes, always return true
    }

    private void processOrder(UUID message) {
        // Implement additional processing logic here
        try {
            Thread.sleep(getRandomWaitTime());
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted");
        }
        System.out.println("Processing record: " + message);
    }

    public static Long getRandomWaitTime() {
        return (long) RANDOM.nextInt((5000 - 1000) + 1);
    }
}
