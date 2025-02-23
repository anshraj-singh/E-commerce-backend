package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    // You can add custom query methods here if needed
}