package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersData")
@Data
public class UserEntry {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
}
