package org.ead.inventorymanagement.repository;

import org.bson.types.ObjectId;
import org.ead.inventorymanagement.model.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Inventory, ObjectId> {

    Optional<Inventory> findById(ObjectId id);
}
