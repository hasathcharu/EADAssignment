package org.ead.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class OrderConfirmedDTO extends OrderDTO {
    private String productId;
    private Double quantity;
    private Double price;
}