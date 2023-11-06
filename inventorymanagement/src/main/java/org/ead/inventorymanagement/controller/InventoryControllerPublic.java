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
@RequestMapping("/api/inventory/public")
@RequiredArgsConstructor
public class InventoryControllerPublic {

    private final InventoryService inventoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductsResponsePublic> getAllProducts(){
        return inventoryService.getAllProductsPublic();
    }

    @GetMapping("/{pId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductsResponsePublic getProductDetails(@PathVariable ObjectId pId){
        return inventoryService.getProductDetailsPublic(pId);
    }

}
