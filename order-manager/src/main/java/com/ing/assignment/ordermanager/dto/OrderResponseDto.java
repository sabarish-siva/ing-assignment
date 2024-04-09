package com.ing.assignment.ordermanager.dto;

import com.ing.assignment.ordercommon.model.OrderLocation;
import com.ing.assignment.ordercommon.model.OrderStatus;
import com.ing.assignment.ordercommon.model.VehicleType;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderResponseDto {
    private UUID id;
    private VehicleType type;
    private Integer quantity;
    private OrderLocation location;
    private OrderStatus status;
}
