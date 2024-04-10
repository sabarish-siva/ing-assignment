package com.ing.assignment.ordermanager.utils.kafka.producer;

import com.ing.assignment.ordercommon.dto.PlaceOrder;
import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordercommon.utils.kafka.producer.AbstractScheduledKafkaProducer;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Producer class for the kafka topic <b>process-car-orders-topic</b>. Extends the
 * {@link AbstractScheduledKafkaProducer} to periodically fetch the
 * {@link com.ing.assignment.ordercommon.model.OrderStatus}.ACCEPTED car
 * orders from the DB and publishes it to the kafka topic. {@link KafkaTemplate}
 * config is located at the {@link com.ing.assignment.ordermanager.config.KafkaConfig} file.
 */
@Component
@Slf4j
public class CarOrderKafkaProducer extends AbstractScheduledKafkaProducer<PlaceOrder> {

    private final OrderDetailsRepository orderDetailsRepository;

    @Value("${spring.kafka.topic.process-car-orders}")
    private String carOrdersTopic;

    public CarOrderKafkaProducer(KafkaTemplate<String, PlaceOrder> kafkaTemplate,
                                            OrderDetailsRepository orderDetailsRepository) {
        super(kafkaTemplate);
        this.orderDetailsRepository = orderDetailsRepository;
    }

    @Override
    protected String getTopicName() {
        return carOrdersTopic;
    }

    @Override
    protected String getTaskName() {
        return "car-orders-producer-task";
    }

    @Override
    public void executeTask() {
        List<OrderDetail> acceptedOrders = orderDetailsRepository.findByIsProcessedAndType(false, VehicleType.CAR);
        if(!acceptedOrders.isEmpty()) {
            log.debug("processing car orders: " + acceptedOrders.size());
            sendMessages(acceptedOrders.stream()
                    .map(it->new PlaceOrder(it.getId(), it.getQuantity())).collect(Collectors.toList()));
            acceptedOrders.forEach(order->order.setProcessed(true));
            orderDetailsRepository.saveAll(acceptedOrders);
        }
    }

}
