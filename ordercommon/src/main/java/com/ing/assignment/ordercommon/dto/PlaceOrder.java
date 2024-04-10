package com.ing.assignment.ordercommon.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Incoming and outgoing dto for the process order topics.
 */
@Data
@NoArgsConstructor
public class PlaceOrder {
    private UUID orderId;
    private Integer quantity;

    public PlaceOrder(UUID id, Integer quantity) {
        this.orderId = id;
        this.quantity = quantity;
    }
}
