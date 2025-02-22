package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = productService.getAllProducts();
        if (allProducts != null && !allProducts.isEmpty()) {
            return new ResponseEntity<>(allProducts, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody Product newProduct) {
        try {
            productService.saveProduct(newProduct);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        Optional<Product> product = productService.getById(productId);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable String productId) {
        productService.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{productId}")
    public ResponseEntity<Product> updateProductById(@PathVariable String productId, @RequestBody Product updatedProduct) {
        Optional<Product> existingProductOpt = productService.getById(productId);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            if (updatedProduct.getName() != null && !updatedProduct.getName().isEmpty()) {
                existingProduct.setName(updatedProduct.getName());
            }
            if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().isEmpty()) {
                existingProduct.setDescription(updatedProduct.getDescription());
            }
            if (updatedProduct.getPrice() >= 0) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            if (updatedProduct.getStock() >= 0) {
                existingProduct.setStock(updatedProduct.getStock());
            }
            productService.saveProduct(existingProduct);
            return new ResponseEntity<>(existingProduct, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}