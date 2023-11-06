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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createProduct(@RequestBody NewProductDTO newProductDTO){
        inventoryService.createProduct(newProductDTO);
        return "Success";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductsResponse> getAllProducts(){
        return inventoryService.getAllProducts();
    }

    @GetMapping("/{pId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDetailsDTO getProductDetails(@PathVariable ObjectId pId){
        return inventoryService.getProductDetails(pId);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public String updateProduct(@RequestBody UpdateProductDTO updateProductDTO){
        inventoryService.updateProduct(updateProductDTO);
        return "Success";
    }

    @DeleteMapping("/{pId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(@PathVariable ObjectId pId){
        inventoryService.deleteProduct(pId);
        return "Success";
    }

    @PutMapping("/place-order")
    @ResponseStatus(HttpStatus.OK)
    public String placeOrder(@RequestBody List<OrderDTO> products){
        return inventoryService.placeOrder(products);
    }

    @PutMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestBody List<OrderDTO> products) {
        boolean result = inventoryService.cancelOrder(products);
        if (!result) {
            return new ResponseEntity<>("Failed to cancel order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Order canceled successfully", HttpStatus.OK);
    }
}
