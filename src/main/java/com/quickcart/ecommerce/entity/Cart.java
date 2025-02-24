package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "cartsData")
@Data
public class Cart {

    @Id
    private String id; // Cart ID
    private String userId; // ID of the user who owns the cart
    private double totalPrice; // Total price of items in the cart

    @DBRef
    private List<Product> productToCart = new ArrayList<>();
}

