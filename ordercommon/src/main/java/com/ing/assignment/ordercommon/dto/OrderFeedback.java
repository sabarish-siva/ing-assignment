package com.ing.assignment.ordercommon.dto;

import com.ing.assignment.ordercommon.model.OrderStatus;
import lombok.Data;

import java.util.UUID;

/**
 * Incoming and outgoing dto for the Feedback topics.
 */
@Data
public class OrderFeedback {
    private UUID orderId;
    private OrderStatus status;
}
