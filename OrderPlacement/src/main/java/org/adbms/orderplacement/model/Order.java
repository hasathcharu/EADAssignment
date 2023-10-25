package org.adbms.orderplacement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String userName;
    private String userAddress;
    private String userTelephone;
    private Date date;
    @Column(unique = true)
    private String orderNumber;
    @Enumerated(value=EnumType.STRING)
    private OrderStatus status;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
