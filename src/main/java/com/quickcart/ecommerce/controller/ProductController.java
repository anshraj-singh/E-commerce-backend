package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.dto.ErrorResponse;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@Tag(name = "Products", description = "Public APIs for browsing products. No authentication required.")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Retrieve list of all available products. This is a public endpoint - no authentication required. " +
                    "Products are cached in Redis for better performance.",
            tags = {"Products"}
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
            summary = "Get product by ID",
            description = "Retrieve detailed information about a specific product. This is a public endpoint. " +
                    "Product data is cached in Redis with 1-hour TTL.",
            tags = {"Products"}
    )
    @GetMapping("/id/{productId}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID (MongoDB ObjectId)", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {
        Optional<Product> product = productService.getById(productId);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}