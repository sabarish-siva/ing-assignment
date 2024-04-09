package com.ing.assignment.ordermanager.dto;

import com.ing.assignment.ordercommon.model.OrderLocation;
import com.ing.assignment.ordercommon.model.VehicleType;
import lombok.Data;

@Data
public class CreateOrderRequestDto {
    private VehicleType type;
    private Integer quantity;
    private OrderLocation location;
}
