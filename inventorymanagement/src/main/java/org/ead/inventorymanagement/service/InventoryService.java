package org.ead.inventorymanagement.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.ead.inventorymanagement.dto.*;
import org.ead.inventorymanagement.exception.RestException;
import org.ead.inventorymanagement.model.Inventory;
import org.ead.inventorymanagement.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;


    public void createProduct(NewProductDTO newProductDTO) {
        Inventory inventory = Inventory.builder()
                .product_name(newProductDTO.getProduct_name())
                .product_brand(newProductDTO.getProduct_brand())
                .price(newProductDTO.getPrice())
                .available_quantity(newProductDTO.getAvailable_quantity())
                .build();

        inventoryRepository.save(inventory);
    }

    public List<ProductsResponse> getAllProducts() {
        List<Inventory> inventories = inventoryRepository.findAll();

        return inventories.stream().map(this::mapToProductsResponse).toList();
    }

    private ProductsResponse mapToProductsResponse(Inventory inventory) {
        return ProductsResponse.builder()
                .pId(inventory.getId().toString())
                .product_name(inventory.getProduct_name())
                .product_brand(inventory.getProduct_brand())
                .price(inventory.getPrice())
                .available_quantity(inventory.getAvailable_quantity())
                .build();
    }

    public ProductDetailsDTO getProductDetails(ObjectId id) {
        Inventory inventory = inventoryRepository.findById(id).orElse(null);
        if (inventory == null) {
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return mapToProductDetailsDTO(inventory);
    }

    private ProductDetailsDTO mapToProductDetailsDTO(Inventory inventory) {
        return ProductDetailsDTO.builder()
                .product_name(inventory.getProduct_name())
                .product_brand(inventory.getProduct_brand())
                .price(inventory.getPrice())
                .available_quantity(inventory.getAvailable_quantity())
                .build();
    }

    public void updateProduct(UpdateProductDTO updateProductDTO) {

        Inventory inventory = inventoryRepository.findById(new ObjectId(updateProductDTO.getProductId())).orElse(null);

        if (inventory == null) {
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        inventory.setProduct_name(updateProductDTO.getProduct_name() != null ? updateProductDTO.getProduct_name() : inventory.getProduct_name());
        inventory.setProduct_brand(updateProductDTO.getProduct_brand() != null ? updateProductDTO.getProduct_brand() : inventory.getProduct_brand());
        Double updatedPrice = updateProductDTO.getPrice();
        Double currentPrice = inventory.getPrice();
        inventory.setPrice(updatedPrice != null ? updatedPrice : currentPrice);

        Double updatedQuantity = updateProductDTO.getAvailable_quantity();
        Double currentQuantity = inventory.getAvailable_quantity();
        inventory.setAvailable_quantity(updatedQuantity != null ? updatedQuantity : currentQuantity);

        inventoryRepository.save(inventory);
    }

    public void deleteProduct(ObjectId id) {
        Inventory inventory = inventoryRepository.findById(id).orElse(null);

        if (inventory == null) {
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        inventoryRepository.deleteById(id);
    }

    public String placeOrder(List<OrderDTO> products) {
        try {
            List<Inventory> inventoryToUpdate = new ArrayList<>();

            for (OrderDTO product : products) {
                Inventory productItem = inventoryRepository.findById(new ObjectId(product.getProductId())).orElse(null);
                if (productItem == null) {
                    throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
                }

                if (productItem.getAvailable_quantity() >= product.getQuantity()) {
                    productItem.setAvailable_quantity(productItem.getAvailable_quantity() - product.getQuantity());
                    inventoryToUpdate.add(productItem);
                } else {
                    throw new Exception("Not in stock");
                    //
                }
            }
            inventoryRepository.saveAll(inventoryToUpdate);
            return "Order placed successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cancelOrder(List<OrderDTO> products) {
        try {
            List<Inventory> inventoryToUpdate = new ArrayList<>();

            for (OrderDTO product : products) {
                Inventory productItem = inventoryRepository.findById(new ObjectId(product.getProductId())).orElse(null);
                if (productItem != null) {
                    productItem.setAvailable_quantity(productItem.getAvailable_quantity() + product.getQuantity());
                    inventoryToUpdate.add(productItem);
                }
            }
            inventoryRepository.saveAll(inventoryToUpdate);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
