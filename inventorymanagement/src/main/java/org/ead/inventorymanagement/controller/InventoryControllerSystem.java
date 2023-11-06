package org.ead.inventorymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.ead.inventorymanagement.dto.*;
import org.ead.inventorymanagement.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/system")
@RequiredArgsConstructor
public class InventoryControllerSystem {

    private final InventoryService inventoryService;

    @PutMapping("/place-order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody List<OrderDTO> products){
        OrderResponseDTO response = inventoryService.placeOrder(products);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @PutMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestBody List<OrderDTO> products) {
        boolean result = inventoryService.cancelOrder(products);
        if (!result) {
            return new ResponseEntity<>("Failed to cancel order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
