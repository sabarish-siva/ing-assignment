package com.ing.assignment.ordercommon.utils.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * This is an abstract producer for kafka topics. Child classes needs to implement the
 * {@link #getTopicName()} and provide with a {@link KafkaTemplate} object. Use
 * {@link #sendMessage(Object)} to send messages of type {@link T} to the kafka topic.
 * Use {@link #sendMessages(List)} to send batch records.
 */
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
