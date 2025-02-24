package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "ordersData")
@Data
public class Order {

    @Id
    private String id;
    private String userId; // ID of the user who placed the order
    private String status; // e.g., "Pending", "Shipped", "Delivered", "Cancelled"
    private double totalAmount; // Total amount for the order

    @DBRef
    private List<Product> orderProducts = new ArrayList<>();
}
