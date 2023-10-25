package org.adbms.orderplacement.dto;

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
    private Long itemId;
    private Long pid;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
