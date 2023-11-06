package org.ead.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateProductDTO {
    private String productId;
    private String product_name;
    private String product_brand;
    private Double price;
    private Double available_quantity;
}
