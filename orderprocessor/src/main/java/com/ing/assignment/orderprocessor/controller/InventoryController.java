package com.ing.assignment.orderprocessor.controller;

import com.ing.assignment.orderprocessor.model.InventoryDetail;
import com.ing.assignment.orderprocessor.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client HTTP entry point for the {@link com.ing.assignment.orderprocessor.OrderProcessorApplication}
 * application. Serves <b>/inventory</b> rest endpoints for the following
 * functionalities-
 *<p>-get all inventory details</p>
 *<p>-UPSERT records to inventory</p>
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<InventoryDetail> getAll() {
        return inventoryService.getAll();
    }

    @PostMapping
    public ResponseEntity<InventoryDetail> upsertInventory(@RequestBody InventoryDetail inventoryDetail) {
        try {
            InventoryDetail response = inventoryService.upsert(inventoryDetail);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
