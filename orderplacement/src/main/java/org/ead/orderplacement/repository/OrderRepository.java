package org.ead.orderplacement.repository;


import org.bson.types.ObjectId;
import org.ead.orderplacement.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAllByUserEmail(String email);
}

