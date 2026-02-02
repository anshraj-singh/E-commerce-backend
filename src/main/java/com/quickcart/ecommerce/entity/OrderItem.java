package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class OrderItem {
    @DBRef
    private Product product; // Reference to the product
    private int quantity; // Quantity ordered
    private double priceAtOrder; // Price at the time of order (in case price changes later)
}