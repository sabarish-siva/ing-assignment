package com.ing.assignment.orderprocessor.utils;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.utils.kafka.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TruckOrderFeedbackProducer extends AbstractKafkaProducer<OrderFeedback> {

    @Value("${spring.kafka.topic.truck-order-feedback}")
    private String truckOrderFeedbackTopic;

    public TruckOrderFeedbackProducer(KafkaTemplate<String, OrderFeedback> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopicName() {
        return truckOrderFeedbackTopic;
    }

    public void sendFeedback(OrderFeedback feedback) {
        sendMessage(feedback);
    }
}

