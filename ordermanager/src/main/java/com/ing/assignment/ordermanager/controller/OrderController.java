package com.ing.assignment.ordermanager.controller;

import com.ing.assignment.ordermanager.dto.OrderResponseDto;
import com.ing.assignment.ordermanager.dto.CreateOrderRequestDto;
import com.ing.assignment.ordermanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Client HTTP entry point for the {@link com.ing.assignment.ordermanager.OrderManagerApplication} application.
 * Serves <b>/order</b> rest endpoints for the following functionalities-
 *<p>-Create Order</p>
 *<p>-Get Order Details by orderId</p>
 *<p>-Get list of all order details</p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("orderId") UUID orderId) {
        OrderResponseDto response = orderService.getOrderById(orderId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        try {
            OrderResponseDto response = orderService.createOrder(createOrderRequestDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
