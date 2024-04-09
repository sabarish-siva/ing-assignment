package com.ing.assignment.orderprocessor.engine;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.orderprocessor.utils.TruckOrderFeedbackProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TruckOrderProcessorEngine extends AbstractScheduledKafkaConsumer<UUID> {

    @Value("${kafka.topic.process-truck-orders}")
    private String truckOrderProcessTopic;

    private final TruckOrderFeedbackProducer truckOrderFeedbackProducer;

    public TruckOrderProcessorEngine(@Qualifier("truckConsumerFactory") ConsumerFactory<String, UUID> consumerFactory,
                                     TruckOrderFeedbackProducer truckOrderFeedbackProducer) {
        super(consumerFactory);
        this.truckOrderFeedbackProducer = truckOrderFeedbackProducer;
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
            truckOrderFeedbackProducer.sendFeedback(feedback);
            performAdditionalProcessing(message);

            feedback.setStatus(OrderStatus.FINISHED);
            // Send feedback again after processing
            truckOrderFeedbackProducer.sendFeedback(feedback);

            commitOffsets();
        }
    }

    private boolean validateRecord(String message) {
        // Implement your validation logic here
        return true; // For demonstration purposes, always return true
    }

    private void performAdditionalProcessing(UUID message) {
        // Implement additional processing logic here
        // For demonstration purposes, let's say we log the processing of the record
        System.out.println("Processing record: " + message);
    }

    private String getFeedbackTopicName() {
        return "car-order-feedback-topic";
    }
}
