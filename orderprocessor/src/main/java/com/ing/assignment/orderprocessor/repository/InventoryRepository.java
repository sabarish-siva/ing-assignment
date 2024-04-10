package com.ing.assignment.orderprocessor.repository;

import com.ing.assignment.ordercommon.model.VehicleType;
import com.ing.assignment.orderprocessor.model.InventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link InventoryDetail} entity
 */
@Repository
public interface InventoryRepository extends JpaRepository<InventoryDetail, UUID> {
    InventoryDetail findOneByType(VehicleType vehicleType);
}
