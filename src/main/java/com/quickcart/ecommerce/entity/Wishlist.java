package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable; // Added
import java.util.ArrayList;
import java.util.List;

@Document(collection = "wishlists")
@Data
public class Wishlist implements Serializable { // Implement this
    private static final long serialVersionUID = 1L; // Add this ID

    @Id
    private String id;
    private String userId;

    @DBRef
    private List<Product> products = new ArrayList<>();
}