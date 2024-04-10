package com.ing.assignment.ordermanager.service;

import com.ing.assignment.ordermanager.dto.OrderResponseDto;
import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordermanager.dto.CreateOrderRequestDto;
import com.ing.assignment.ordermanager.repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for the {@link com.ing.assignment.ordermanager.controller.OrderController}.
 * Connects to the {@link OrderDetailsRepository} and returns {@link OrderResponseDto} objects.
 */
@Service
public class OrderService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public List<OrderResponseDto> getAllOrders() {
        List<OrderDetail> orderDetailList = orderDetailsRepository.findAll();
        return orderDetailList.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    public OrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setType(createOrderRequestDto.getType());
        orderDetail.setStatus(OrderStatus.ACCEPTED);
        orderDetail.setQuantity(createOrderRequestDto.getQuantity());
        orderDetail.setProcessed(false);
        orderDetail.setLocation(createOrderRequestDto.getLocation());
        orderDetailsRepository.save(orderDetail);

        return new OrderResponseDto(orderDetail);
    }

    public OrderResponseDto getOrderById(UUID orderId) {
        Optional<OrderDetail> optionalOrder = orderDetailsRepository.findById(orderId);
        return optionalOrder.map(OrderResponseDto::new).orElse(null);
    }
}
