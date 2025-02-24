package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.CartService;
import com.quickcart.ecommerce.service.ProductService;
import com.quickcart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // Get user's cart by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId) {
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return cart.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Add product to cart using productId
    @PostMapping("/user/{userId}/addItem/{productId}")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String userId, @PathVariable String productId) {
        Optional<Product> productOpt = productService.getById(productId);

        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Product not found
        }

        Cart updatedCart = cartService.addProductToCart(userId, productOpt.get());
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    // Remove product from cart
    @DeleteMapping("/user/{userId}/removeItem/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        cartService.removeProductFromCart(userId, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
