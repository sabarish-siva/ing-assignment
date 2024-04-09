package com.ing.assignment.ordermanager.utils.kafka.producer;

import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordercommon.utils.kafka.producer.AbstractScheduledKafkaProducer;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarOrderKafkaProducer extends AbstractScheduledKafkaProducer<UUID> {

    private final OrderDetailsRepository orderDetailsRepository;

    @Value("${kafka.topic.process-car-orders}")
    private String carOrdersTopic;

    public CarOrderKafkaProducer(KafkaTemplate<String, UUID> kafkaTemplate,
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
            sendMessages(acceptedOrders.stream().map(OrderDetail::getId).collect(Collectors.toList()));
            acceptedOrders.forEach(order->order.setProcessed(true));
            orderDetailsRepository.saveAll(acceptedOrders);
        }
    }

}
