package org.ead.orderplacement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class InventoryResponseDTO {
    private boolean success;
    private List<InventoryConfirmDTO> products;
    private List<String> failedProducts;
}