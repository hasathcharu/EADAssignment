package org.adbms.orderplacement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
  private String userEmail;
  private String userTelephone;
  private List<OrderItemDTO> orderItems;
}
