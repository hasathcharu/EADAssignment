package org.ead.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductsResponse {
    private String pId;
    private String product_name;
    private String product_brand;
    private BigDecimal price;
    private Double available_quantity;
}
