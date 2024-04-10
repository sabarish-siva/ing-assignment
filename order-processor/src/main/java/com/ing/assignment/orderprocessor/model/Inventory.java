package com.ing.assignment.orderprocessor.model;

import com.ing.assignment.ordercommon.model.VehicleType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private Integer quantity;
}
