package com.ing.assignment.ordercommon.utils.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public abstract class AbstractScheduledKafkaConsumer<T> extends AbstractKafkaConsumer<T> {

    private static final long DEFAULT_DELAY_MS = 5000;

    public AbstractScheduledKafkaConsumer(ConsumerFactory<String, T> consumerFactory) {
        super(consumerFactory);
    }

    protected abstract String getTaskName();

    protected abstract void processMessages();

    @Scheduled(fixedDelay = DEFAULT_DELAY_MS)
    public void execute() {
        log.info("Executing task: {}", getTaskName());
        processMessages();
    }

}
