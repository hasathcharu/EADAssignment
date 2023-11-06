package org.ead.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductsResponse {
    private String pId;
    private String product_name;
    private String product_brand;
    private double price;
    private double available_quantity;
}
