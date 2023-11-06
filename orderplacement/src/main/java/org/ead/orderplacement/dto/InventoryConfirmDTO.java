package org.ead.orderplacement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryConfirmDTO {
    private String productId;
    private Double quantity;
    private BigDecimal price;
}
