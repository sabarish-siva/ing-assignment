package com.ing.assignment.ordercommon.utils.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;
import java.util.Collections;

/**
 * This is an abstract consumer class to consume from the kafka topics. Child classes needs
 * to implement the {@link #getTopicName()} and provide with a {@link ConsumerFactory} object
 * to the constructor. At the time of writing this doc, kafka configs in both services
 * doesn't have auto commit. Use {@link #pollRecords()} to get records individually and call
 * {@link #commitOffsets()} to sync the offsets.
 **/
@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    protected final KafkaConsumer<String, T> kafkaConsumer;
    private static final int DEFAULT_POLL_TIME = 1000;

    public AbstractKafkaConsumer(ConsumerFactory<String, T> consumerFactory) {
        this.kafkaConsumer = (KafkaConsumer<String, T>) consumerFactory.createConsumer();
        this.kafkaConsumer.subscribe(Collections.singleton(getTopicName()));
    }

    protected abstract String getTopicName();

    /**
     * Polls records with the default poll time. Call the {@link #commitOffsets()} method to
     * commit the call as auto commit is disabled.
     */
    protected ConsumerRecords<String, T> pollRecords() {
        log.debug("polling from topic" + getTopicName());
        return kafkaConsumer.poll(Duration.ofMillis(DEFAULT_POLL_TIME));
    }

    protected void commitOffsets() {
        log.debug("committing offsets..");
        kafkaConsumer.commitSync();
        log.debug("commit successful");
    }
}

