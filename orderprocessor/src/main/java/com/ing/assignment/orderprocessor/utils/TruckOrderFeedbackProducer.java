package com.ing.assignment.orderprocessor.utils;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.utils.kafka.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer class for the kafka topic <b>truck-order-feedback-topic</b>. Extends the basic
 * {@link AbstractKafkaProducer} class to send records. {@link KafkaTemplate} bean can be
 * found at {@link com.ing.assignment.orderprocessor.config.KafkaConfig} file. Initialised
 * and used in the {@link com.ing.assignment.orderprocessor.engine.TruckOrderProcessorEngine}
 * to send feedback to {@link com.ing.assignment.ordermanager} service
 * via the kafka topic mentioned.
 */
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

