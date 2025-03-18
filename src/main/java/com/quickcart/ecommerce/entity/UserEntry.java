package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "usersData")
@Data
public class UserEntry {

    @Id
    private String id;
    private String username;
    private String password; // Store hashed password
    private String email;
    private String address;
    private String phoneNumber;
    private List<String> roles; // e.g., ["ROLE_USER", "ROLE_ADMIN"]

    @DBRef
    private List<Cart> carts = new ArrayList<>(); // List of user cart product add

    @DBRef
    private List<Order> orders = new ArrayList<>(); // List of user orders
}
