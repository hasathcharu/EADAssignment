package com.hasathcharu.ProductService.repository;

import com.hasathcharu.ProductService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
