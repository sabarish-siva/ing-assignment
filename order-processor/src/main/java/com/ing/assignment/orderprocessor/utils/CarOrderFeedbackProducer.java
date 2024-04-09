package com.ing.assignment.orderprocessor.utils;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.utils.kafka.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CarOrderFeedbackProducer extends AbstractKafkaProducer<OrderFeedback> {

    @Value("${kafka.topic.car-order-feedback}")
    private String carOrderFeedbackTopic;

    public CarOrderFeedbackProducer(KafkaTemplate<String, OrderFeedback> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopicName() {
        return carOrderFeedbackTopic;
    }

    public void sendFeedback(OrderFeedback feedback) {
        sendMessage(feedback);
    }
}

