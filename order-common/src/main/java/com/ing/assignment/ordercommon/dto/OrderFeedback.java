package com.ing.assignment.ordercommon.dto;

import com.ing.assignment.ordercommon.model.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderFeedback {
    private UUID orderId;
    private OrderStatus status;
}
