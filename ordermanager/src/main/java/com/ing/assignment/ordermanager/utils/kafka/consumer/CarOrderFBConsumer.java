package com.ing.assignment.ordermanager.utils.kafka.consumer;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Consumer class for the kafka topic <b>car-orders-feedback-topic</b>. Extends the
 * {@link AbstractScheduledKafkaConsumer} to fetch periodically from the topic and
 * update the database the current status {@link com.ing.assignment.ordercommon.model.OrderStatus} of the order (PROCESSING, FINISHED).
 * Supplies the parent with {@link ConsumerFactory} object. {@link org.springframework.context.annotation.Bean}
 * for carFBConsumerFactory can be found at {@link com.ing.assignment.ordermanager.config.KafkaConfig} file.
 */
@Component
public class CarOrderFBConsumer extends AbstractScheduledKafkaConsumer<Object> {

    private final OrderDetailsRepository orderDetailsRepository;

    public CarOrderFBConsumer(@Qualifier("carFBConsumerFactory") ConsumerFactory<String, Object> consumerFactory,
                              OrderDetailsRepository orderDetailsRepository) {
        super(consumerFactory);
        this.orderDetailsRepository = orderDetailsRepository;
    }

    @Override
    protected String getTaskName() {
        return "Processing Car Feedback messages";
    }

    @Override
    protected String getTopicName() {
        return "car-orders-feedback-topic";
    }

    @Override
    protected void processMessages() {
        ConsumerRecords<String, Object> records = pollRecords();
        for (ConsumerRecord<String, Object> record : records) {
            processRecord(record);
        }
    }

    private void processRecord(ConsumerRecord<String, Object> record) {
        OrderFeedback feedback = (OrderFeedback) record.value();

        processOrder(feedback);
        commitOffsets();
    }

    private void processOrder(OrderFeedback feedback) {
        Optional<OrderDetail> optionalOrder = orderDetailsRepository.findById(feedback.getOrderId());
        if(optionalOrder.isPresent()) {
            OrderDetail orderDetail = optionalOrder.get();
            orderDetail.setStatus(feedback.getStatus());
            orderDetailsRepository.save(orderDetail);
        }
    }
}
