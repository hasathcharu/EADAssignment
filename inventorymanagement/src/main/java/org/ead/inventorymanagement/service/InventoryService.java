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


    public ProductsResponse createProduct(NewProductDTO newProductDTO) {
        Inventory inventory = Inventory.builder()
                .product_name(newProductDTO.getProduct_name())
                .product_brand(newProductDTO.getProduct_brand())
                .price(newProductDTO.getPrice())
                .available_quantity(newProductDTO.getAvailable_quantity())
                .build();

        inventoryRepository.save(inventory);
        return mapToProductsResponse(inventory);
    }

    public List<ProductsResponse> getAllProducts() {
        List<Inventory> inventories = inventoryRepository.findAll();

        return inventories.stream().map(this::mapToProductsResponse).toList();
    }
    public List<ProductsResponsePublic> getAllProductsPublic() {
        List<Inventory> inventories = inventoryRepository.findAll();

        return inventories.stream().map(this::mapToProductsResponsePublic).toList();
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
    private ProductsResponsePublic mapToProductsResponsePublic(Inventory inventory) {
        return ProductsResponsePublic.builder()
                .pId(inventory.getId().toString())
                .product_name(inventory.getProduct_name())
                .product_brand(inventory.getProduct_brand())
                .price(inventory.getPrice())
                .qty_available(inventory.getAvailable_quantity() > 0)
                .build();
    }

    public ProductsResponse getProductDetails(String id) {
        Inventory inventory;
        System.out.println("Hello");
        try {
            inventory = inventoryRepository.findById(new ObjectId(id)).orElseThrow();
            System.out.println("Hello Inventory");
        }catch(Exception e){
            System.out.println("Hello Exception");
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return mapToProductsResponse(inventory);
    }

    public ProductsResponsePublic getProductDetailsPublic(String id) {
        Inventory inventory;
        try {
            inventory = inventoryRepository.findById(new ObjectId(id)).orElseThrow();
        }catch(Exception e){
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return mapToProductsResponsePublic(inventory);
    }


    public ProductsResponse updateProduct(UpdateProductDTO updateProductDTO) {

        Inventory inventory;
        try {
            inventory = inventoryRepository.findById(new ObjectId(updateProductDTO.getProductId())).orElseThrow();
        }catch(Exception e){
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        inventory.setProduct_name(updateProductDTO.getProduct_name() != null ? updateProductDTO.getProduct_name() : inventory.getProduct_name());
        inventory.setProduct_brand(updateProductDTO.getProduct_brand() != null ? updateProductDTO.getProduct_brand() : inventory.getProduct_brand());
        inventory.setPrice(updateProductDTO.getPrice() != null ? updateProductDTO.getPrice() : inventory.getPrice());
        inventory.setAvailable_quantity(updateProductDTO.getAvailable_quantity() != null ? updateProductDTO.getAvailable_quantity() : inventory.getAvailable_quantity());

        inventoryRepository.save(inventory);
        return mapToProductsResponse(inventory);
    }

    public void deleteProduct(String id) {
        try {
            inventoryRepository.findById(new ObjectId(id)).orElseThrow();
        }catch(Exception e){
            throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
        }
        inventoryRepository.deleteById(new ObjectId(id));
    }

    public OrderResponseDTO placeOrder(List<OrderDTO> products) {

        OrderResponseDTO response = new OrderResponseDTO();
        List<String> productsWithInsufficientStock = new ArrayList<>();
        List<OrderConfirmedDTO> productsWithSufficientStock = new ArrayList<>();
        List<Inventory> inventoryToUpdate = new ArrayList<>();


        for (OrderDTO product : products) {
            ObjectId productId;
            try {
                productId = new ObjectId(product.getProductId());
            }catch(Exception e){
                throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
            }
            Inventory productItem = inventoryRepository.findById(productId).orElse(null);
            if (productItem == null) {
                throw new RestException(HttpStatus.NOT_FOUND, "Product not found");
            } else if (productItem.getAvailable_quantity() >= product.getQuantity()) {
                productItem.setAvailable_quantity(productItem.getAvailable_quantity() - product.getQuantity());
                inventoryToUpdate.add(productItem);
                productsWithSufficientStock.add(mapToOrderConfirmedDTO(productItem, product.getQuantity()));
            } else {
                productsWithInsufficientStock.add(product.getProductId());
            }
        }

        if (!productsWithInsufficientStock.isEmpty()) {
            response.setSuccess(false);
            response.setFailedProducts(productsWithInsufficientStock);
        } else {
            inventoryRepository.saveAll(inventoryToUpdate);
            response.setSuccess(true);
            response.setProducts(productsWithSufficientStock);
        }

        return response;
    }

    public boolean cancelOrder(List<OrderDTO> products) {
        try {
            List<Inventory> inventoryToUpdate = new ArrayList<>();

            for (OrderDTO product : products) {
                ObjectId productId;
                try {
                    productId = new ObjectId(product.getProductId());
                }catch(Exception e){
                    continue;
                }
                Inventory productItem = inventoryRepository.findById(productId).orElse(null);
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

    private OrderConfirmedDTO mapToOrderConfirmedDTO(Inventory inventory, Double quantity) {
        return OrderConfirmedDTO.builder()
                .productId(inventory.getId().toString())
                .price(inventory.getPrice())
                .quantity(quantity)
                .build();
    }
}
