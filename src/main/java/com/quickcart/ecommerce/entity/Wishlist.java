package com.quickcart.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "wishlists")
@Data
public class Wishlist {
    @Id
    private String id; // Wishlist ID
    private String userId; // ID of the user who owns the wishlist

    @DBRef
    private List<Product> products = new ArrayList<>(); // List of products in the wishlist
}