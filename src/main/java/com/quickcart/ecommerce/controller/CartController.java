package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Cart.CartItem;
import com.quickcart.ecommerce.service.CartService;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId) {
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return cart.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/user/{userId}/addItem")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String userId, @RequestBody CartItem newItem) {
        // Retrieve the cart for the user
        Cart cart = cartService.getCartByUserId(userId).orElse(new Cart());

        // If the cart is new, set the userId
        if (cart.getId() == null) {
            cart.setUserId(userId);
        }

        // Add the new item to the cart
        cartService.addItemToCart(userId, newItem);

        // Save the updated cart
        cartService.saveCart(cart);

        // Return the updated cart
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/removeItem/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        cartService.removeItemFromCart(userId, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}