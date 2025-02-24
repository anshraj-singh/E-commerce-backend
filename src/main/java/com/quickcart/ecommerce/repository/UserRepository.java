package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.UserEntry;
import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntry,String> {
    UserEntry findByUsername(String username); // Corrected field name
}
