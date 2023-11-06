package org.ead.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderResponseDTO {
    private boolean success;
    private List<OrderConfirmedDTO> products;
    private List<String> failedProducts;
}
