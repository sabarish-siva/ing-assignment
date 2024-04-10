package com.ing.assignment.ordercommon.utils.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;
import java.util.Collections;

public abstract class AbstractKafkaConsumer<T> {

    protected final KafkaConsumer<String, T> kafkaConsumer;
    private static final int DEFAULT_POLL_TIME = 1000;

    public AbstractKafkaConsumer(ConsumerFactory<String, T> consumerFactory) {
        this.kafkaConsumer = (KafkaConsumer<String, T>) consumerFactory.createConsumer();
        this.kafkaConsumer.subscribe(Collections.singleton(getTopicName()));
    }

    protected abstract String getTopicName();

    protected ConsumerRecords<String, T> pollRecords() {
        return kafkaConsumer.poll(Duration.ofMillis(DEFAULT_POLL_TIME));
    }

    protected void commitOffsets() {
        kafkaConsumer.commitSync();
    }
}

