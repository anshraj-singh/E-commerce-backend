package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable; // Added

@Document(collection = "productsData")
@Data
public class Product implements Serializable { // Implement this
    private static final long serialVersionUID = 1L; // Add this ID

    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
}