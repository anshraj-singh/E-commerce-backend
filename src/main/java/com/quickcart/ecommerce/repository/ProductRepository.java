package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    // You can add custom query methods here if needed
}