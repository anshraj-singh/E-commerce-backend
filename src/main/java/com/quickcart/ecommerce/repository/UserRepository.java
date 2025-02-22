package com.quickcart.ecommerce.repository;

import com.quickcart.ecommerce.entity.UserEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntry,String> {

}
