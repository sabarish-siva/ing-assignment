package com.ing.assignment.ordermanager.service;

import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordermanager.dto.CreateOrderRequestDto;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public List<OrderDetail> getAllOrders() {
        log.info("-----------------inside get all orders");
        return orderDetailsRepository.findAll();
    }

    public OrderDetail createOrder(CreateOrderRequestDto createOrderRequestDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setType(createOrderRequestDto.getType());
        orderDetail.setStatus(OrderStatus.ACCEPTED);
        orderDetail.setQuantity(createOrderRequestDto.getQuantity());
        orderDetail.setProcessed(false);
        orderDetail.setLocation(createOrderRequestDto.getLocation());
        return orderDetailsRepository.save(orderDetail);
    }

    public OrderDetail getOrderById(UUID orderId) {
        Optional<OrderDetail> optionalOrder = orderDetailsRepository.findById(orderId);
        return optionalOrder.orElse(null);
    }
}
