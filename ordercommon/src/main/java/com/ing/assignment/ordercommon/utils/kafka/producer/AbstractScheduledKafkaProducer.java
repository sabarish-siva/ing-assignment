package com.ing.assignment.ordercommon.utils.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * This is an abstract scheduler class extending the {@link AbstractKafkaProducer} class. Implement the
 * {@link #executeTask()} method which will be called at specific delays. Supply with the {@link KafkaTemplate},
 * {@link #getTopicName()} and {@link #getTaskName()}. Utilise the {@link #sendMessage(Object)} or
 * {@link #sendMessages(List)} method from parent inside {@link #executeTask()} function to send periodic records to kafka.
 */
@Slf4j
public abstract class AbstractScheduledKafkaProducer<T> extends AbstractKafkaProducer<T> {

    private static final long DEFAULT_DELAY_MS = 5000;

    public AbstractScheduledKafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        super(kafkaTemplate);
    }

    protected abstract String getTaskName();
    protected abstract void executeTask();
    @Scheduled(fixedDelay = DEFAULT_DELAY_MS)
    public void execute() {
        log.info("Executing task : " +getTaskName());
        executeTask();
    }
}
