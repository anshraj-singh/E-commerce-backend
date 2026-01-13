package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.OrderService;
import com.quickcart.ecommerce.service.ProductService;
import com.quickcart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntry>> getAllUsers() {
        List<UserEntry> all = userService.getAllUser ();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String myId) {
        userService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createUser(@RequestBody UserEntry admin){
        try {
            userService.saveAdmin(admin);
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        if (!allOrders.isEmpty()) {
            return new ResponseEntity<>(allOrders, HttpStatus.OK);
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
