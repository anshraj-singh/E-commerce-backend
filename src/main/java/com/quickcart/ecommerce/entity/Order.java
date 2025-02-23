package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "ordersData")
@Data
public class Order {

    @Id
    private String id;
    private String userId; // ID of the user who placed the order
    private List<OrderItem> items; // List of products in the order
    private String status; // e.g., "Pending", "Shipped", "Delivered", "Cancelled"
    private double totalAmount; // Total amount for the order

    // Inner class to represent an item in the order
    @Data
    public static class OrderItem {
        private String productId; // ID of the product
        private int quantity; // Quantity of the product ordered
        private double price; // Price of the product at the time of order
    }
}