package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable; // Added
import java.util.ArrayList;
import java.util.List;

@Document(collection = "usersData")
@Data
public class UserEntry implements Serializable { // Implement this
    private static final long serialVersionUID = 1L; // Add this ID

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String address;
    private String phoneNumber;
    private List<String> roles;

    @DBRef
    private List<Cart> carts = new ArrayList<>();

    @DBRef
    private List<Order> orders = new ArrayList<>();
}