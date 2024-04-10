package com.ing.assignment.orderprocessor.service;

import com.ing.assignment.orderprocessor.model.InventoryDetail;
import com.ing.assignment.orderprocessor.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Simple service layer for the {@link com.ing.assignment.orderprocessor.controller.InventoryController} endpoints
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<InventoryDetail> getAll() {
        return inventoryRepository.findAll();
    }

    public InventoryDetail upsert(InventoryDetail incoming) {
        InventoryDetail inventoryDetail = inventoryRepository.findOneByType(incoming.getType());
        if (inventoryDetail !=null) {
            inventoryDetail.setQuantity(incoming.getQuantity());
        } else {
            inventoryDetail = incoming;
        }
        return inventoryRepository.save(inventoryDetail);
    }
}
