package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "cartsData")
@Data
public class Cart {

    @Id
    private String id; // Cart ID
    private String userId; // ID of the user who owns the cart
    private List<CartItem> items; // List of items in the cart
    private double totalPrice; // Total price of items in the cart

    // Inner class to represent an item in the cart
    @Data
    public static class CartItem {
        private String productId; // ID of the product
        private int quantity; // Quantity of the product in the cart
        private double price; // Price of the product
    }
}