package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j // Using Lombok for better logging
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * LOGIC:
     * 1. Check Redis for "products::productId".
     * 2. If found (Cache Hit), return immediately.
     * 3. If NOT found (Cache Miss), execute method to fetch from MongoDB.
     * 4. Store result in Redis for 2 minutes and return.
     */
    @Cacheable(value = "products", key = "#productId")
    public Optional<Product> getById(String productId) {
        log.info("### CACHE MISS: Fetching Product {} from MongoDB", productId);
        return productRepository.findById(productId);
    }

    @Cacheable(value = "allProducts")
    public List<Product> getAllProducts() {
        log.info("### CACHE MISS: Fetching all products from MongoDB");
        return productRepository.findAll();
    }

    /**
     * When saving/updating, we use @CacheEvict to remove the old data.
     * This ensures the "Next" get request fetches the fresh data from the DB.
     */
    @CacheEvict(value = "products", key = "#product.id")
    public void saveProduct(Product product) {
        productRepository.save(product);
        log.info("### CACHE EVICTED: Product {} updated, cache cleared", product.getId());
    }

    @CacheEvict(value = "products", key = "#productId")
    public void deleteById(String productId) {
        productRepository.deleteById(productId);
        log.info("### CACHE EVICTED: Product {} deleted", productId);
    }
}