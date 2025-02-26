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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @GetMapping("/me")
    public ResponseEntity<Cart> getCartByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Cart> cart = cartService.getCartByUserId(user.getId());
        return cart.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Add product to cart using productId
    @PostMapping("/addItem/{productId}")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Product> productOpt = productService.getById(productId);
        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Product not found
        }

        Cart updatedCart = cartService.addProductToCart(user.getId(), productOpt.get());
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    // Remove product from cart
    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        cartService.removeProductFromCart(user.getId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
