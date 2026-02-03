package com.quickcart.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "usersData")
@Data
@Schema(description = "User account information")
public class UserEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "Unique user ID (auto-generated)", example = "65abc123def456789012", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Unique username for login", example = "johndoe", required = true, minLength = 3, maxLength = 50)
    private String username;

    @Schema(description = "User password", example = "SecurePass123!", required = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Ye use karein @JsonIgnore ki jagah
    private String password;

    @Schema(description = "User email address", example = "john.doe@example.com", required = true, format = "email")
    private String email;

    @Schema(description = "User's delivery address", example = "123 Main Street, Mumbai, Maharashtra, 400001")
    private String address;

    @Schema(description = "User's contact phone number", example = "+91-9876543210", pattern = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;

    @Schema(description = "User roles/permissions", example = "[\"USER\"]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> roles;

    @DBRef
    @JsonIgnore
    @Schema(hidden = true)
    private List<Cart> carts = new ArrayList<>();

    @DBRef
    @JsonIgnore
    @Schema(hidden = true)
    private List<Order> orders = new ArrayList<>();
}