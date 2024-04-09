package com.ing.assignment.ordercommon.utils.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

public abstract class AbstractKafkaProducer<T> {

    protected final KafkaTemplate<String, T> kafkaTemplate;

    public AbstractKafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopicName();

    protected void sendMessage(T message) {
        kafkaTemplate.send(getTopicName(), message);
    }

    protected void sendMessages(List<T> messages) {
        messages.forEach(message -> kafkaTemplate.send(getTopicName(), message));
    }
}
