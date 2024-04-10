package com.ing.assignment.ordercommon.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PlaceOrder {
    private UUID orderId;
    private Integer quantity;

    public PlaceOrder(UUID id, Integer quantity) {
        this.orderId = id;
        this.quantity = quantity;
    }
}
