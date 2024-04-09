package com.ing.assignment.ordermanager.utils.kafka.consumer;

import com.ing.assignment.ordercommon.dto.OrderFeedback;
import com.ing.assignment.ordercommon.utils.kafka.consumer.AbstractScheduledKafkaConsumer;
import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CarOrderFBConsumer extends AbstractScheduledKafkaConsumer<Object> {

    @Value("${kafka.topic.car-order-feedback}")
    private String carOrderFeedbackTopic;

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
        ConsumerRecords<String, Object> records = pollRecords(Optional.empty());
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
        // Implement additional processing logic here
        Optional<OrderDetail> optionalOrder = orderDetailsRepository.findById(feedback.getOrderId());
        if(optionalOrder.isPresent()) {
            OrderDetail orderDetail = optionalOrder.get();
            orderDetail.setStatus(feedback.getStatus());
            orderDetailsRepository.save(orderDetail);
        }
    }
}
