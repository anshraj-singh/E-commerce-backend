package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    Wishlist findByUserId(String userId);
}