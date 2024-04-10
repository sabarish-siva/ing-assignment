package com.ing.assignment.ordermanager.dto;

import com.ing.assignment.ordercommon.model.OrderLocation;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.ordermanager.model.OrderDetail;
import lombok.Data;

import java.util.UUID;

/**
 * Outgoing dto for {@link OrderDetail} entity.
 */
@Data
public class OrderResponseDto {
    private UUID orderId;
    private VehicleType type;
    private Integer quantity;
    private OrderLocation location;
    private OrderStatus status;

    public OrderResponseDto(OrderDetail orderDetail) {
        this.orderId = orderDetail.getId();
        this.type = orderDetail.getType();
        this.quantity = orderDetail.getQuantity();
        this.location = orderDetail.getLocation();
        this.status = orderDetail.getStatus();
    }
}
