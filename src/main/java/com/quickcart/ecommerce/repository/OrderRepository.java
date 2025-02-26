package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    // You can add custom query methods here if needed
    List<Order> findByUserId(String userId); // Custom query method to find orders by user ID

}