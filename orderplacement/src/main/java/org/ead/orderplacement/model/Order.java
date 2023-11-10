package org.ead.orderplacement.model;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(value = "order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private ObjectId id;
    private String userEmail;
    private String userName;
    private String userAddress;
    private String userTelephone;
    private Date date;
    @Indexed(unique = true)
    private String orderNumber;
    @Enumerated(value=EnumType.STRING)
    private OrderStatus status;
    private List<OrderItem> orderItems;
    private String deliveryPersonName;
    private String deliveryPersonEmail;
    private String deliveryPersonTelephone;
}
