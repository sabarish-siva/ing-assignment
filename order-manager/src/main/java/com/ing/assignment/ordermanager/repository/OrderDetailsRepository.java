package com.ing.assignment.ordermanager.repository;

import com.ing.assignment.ordermanager.model.OrderDetail;
import com.ing.assignment.ordercommon.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderDetailsRepository extends JpaRepository<OrderDetail, UUID> {
    List<OrderDetail> findByIsProcessedAndType(boolean isProcessed, VehicleType type);
}
