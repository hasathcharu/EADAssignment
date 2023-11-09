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
@RequestMapping("/api/inventory/admin")
@RequiredArgsConstructor
public class InventoryControllerAdmin {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductsResponse createProduct(@RequestBody NewProductDTO newProductDTO){
        return inventoryService.createProduct(newProductDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductsResponse> getAllProducts(){
        return inventoryService.getAllProducts();
    }

    @GetMapping("/{pId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductsResponse getProductDetails(@PathVariable String pId){
        return inventoryService.getProductDetails(pId);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public ProductsResponse updateProduct(@RequestBody UpdateProductDTO updateProductDTO){
        return inventoryService.updateProduct(updateProductDTO);
    }

    @DeleteMapping("/{pId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(@PathVariable String pId){
        inventoryService.deleteProduct(pId);
        return "Success";
    }

}
