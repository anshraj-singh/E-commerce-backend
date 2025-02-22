package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(String productId) {
        return productRepository.findById(productId);
    }

    public void deleteById(String productId) {
        productRepository.deleteById(productId);
    }
}