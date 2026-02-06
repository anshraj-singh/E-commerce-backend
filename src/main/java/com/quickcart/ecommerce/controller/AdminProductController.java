package com.quickcart.ecommerce.controller;


import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/product")
@Tag(name = "Admin - Product Management", description = "Admin-only APIs for product CRUD operations. Requires ADMIN role.")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "[ADMIN] Get all products",
            description = "Retrieve complete list of all products in the system. " +
                    "Restricted to ADMIN role. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - Product Management"}
    )
    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = productService.getAllProducts();
        if (allProducts != null && !allProducts.isEmpty()) {
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "[ADMIN] Create new product",
            description = "Add a new product to the catalog. Product cache will be cleared automatically. " +
                    "Restricted to ADMIN role. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - Product Management"}
    )
    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details to create",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    name = "Create Product",
                                    value = "{\n" +
                                            "  \"name\": \"Wireless Headphones\",\n" +
                                            "  \"description\": \"Premium noise-cancelling wireless headphones\",\n" +
                                            "  \"price\": 4999.99,\n" +
                                            "  \"stock\": 50\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Product newProduct) {
        try {
            productService.saveProduct(newProduct);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "[ADMIN] Get product by ID",
            description = "Retrieve detailed information about a specific product. " +
                    "Restricted to ADMIN role. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - Product Management"}
    )
    @GetMapping("/id/{productId}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {
        Optional<Product> product = productService.getById(productId);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "[ADMIN] Update product",
            description = "Update existing product details. Product cache will be cleared automatically. " +
                    "Restricted to ADMIN role. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - Product Management"}
    )
    @PutMapping("/id/{productId}")
    public ResponseEntity<?> updateProductById(
            @Parameter(description = "Product ID to update", required = true, example = "65abc123def456789012")
            @PathVariable String productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"name\": \"Updated Product Name\",\n" +
                                            "  \"description\": \"Updated description\",\n" +
                                            "  \"price\": 5999.99,\n" +
                                            "  \"stock\": 75\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Product updatedProduct) {
        Optional<Product> existingProductOpt = productService.getById(productId);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStock(updatedProduct.getStock());
            productService.saveProduct(existingProduct);
            return new ResponseEntity<>(existingProduct, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "[ADMIN] Delete product",
            description = "Permanently delete a product from the catalog. Product cache will be cleared automatically. " +
                    "This action cannot be undone. Restricted to ADMIN role. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - Product Management"}
    )
    @DeleteMapping("/id/{productId}")
    public ResponseEntity<?> deleteProductById(
            @Parameter(description = "Product ID to delete", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {
        productService.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}