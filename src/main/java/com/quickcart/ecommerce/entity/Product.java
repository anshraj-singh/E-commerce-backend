package com.quickcart.ecommerce.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "productsData")
@Data
@Schema(description = "Product catalog information")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "Unique product ID (auto-generated)", example = "65abc123def456789012", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Product name/title", example = "Wireless Bluetooth Headphones", required = true, minLength = 3, maxLength = 200)
    private String name;

    @Schema(description = "Detailed product description", example = "Premium quality wireless headphones with active noise cancellation and 30-hour battery life", maxLength = 2000)
    private String description;

    @Schema(description = "Product price in INR", example = "4999.99", required = true, minimum = "0", exclusiveMinimum = true)
    private double price;

    @Schema(description = "Available stock quantity", example = "50", required = true, minimum = "0")
    private int stock;
}