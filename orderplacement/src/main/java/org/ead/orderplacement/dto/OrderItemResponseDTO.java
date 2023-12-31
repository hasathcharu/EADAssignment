package org.ead.orderplacement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private String pid;
    private Double qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
