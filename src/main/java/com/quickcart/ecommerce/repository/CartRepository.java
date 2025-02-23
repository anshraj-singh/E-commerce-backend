package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByUserId(String userId); // Custom query to find cart by user ID
}