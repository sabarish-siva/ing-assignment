package com.ing.assignment.ordercommon.utils.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

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
