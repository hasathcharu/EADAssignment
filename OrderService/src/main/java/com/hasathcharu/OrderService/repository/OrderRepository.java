package com.hasathcharu.OrderService.repository;

import com.hasathcharu.OrderService.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
