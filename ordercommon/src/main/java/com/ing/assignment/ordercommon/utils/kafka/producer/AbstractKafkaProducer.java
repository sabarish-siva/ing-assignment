package com.ing.assignment.ordercommon.utils.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * This is an abstract producer for kafka topics. Child classes needs to implement the
 * {@link #getTopicName()} and provide with a {@link KafkaTemplate} object. Use
 * {@link #sendMessage(Object)} to send messages of type {@link T} to the kafka topic.
 * Use {@link #sendMessages(List)} to send batch records.
 */
@Slf4j
public abstract class AbstractKafkaProducer<T> {

    protected final KafkaTemplate<String, T> kafkaTemplate;

    public AbstractKafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopicName();

    protected void sendMessage(T message) {
        log.debug("Writing to kafka topic : "+ getTopicName());
        kafkaTemplate.send(getTopicName(), message);
    }

    protected void sendMessages(List<T> messages) {
        log.debug("Writing to kafka topic : "+ getTopicName());
        messages.forEach(message -> kafkaTemplate.send(getTopicName(), message));
    }
}
