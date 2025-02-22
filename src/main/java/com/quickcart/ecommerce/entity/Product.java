package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productsData")
@Data
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock; // Quantity available in stock
}