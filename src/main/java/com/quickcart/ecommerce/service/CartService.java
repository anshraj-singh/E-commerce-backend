package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Cart.CartItem;
import com.quickcart.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(String userId) {
        return Optional.ofNullable(cartRepository.findByUserId(userId));
    }

    public void addItemToCart(String userId, CartItem newItem) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getItems().add(newItem);
            updateTotalPrice(cart);
            cartRepository.save(cart);
        }
    }

    public void removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) return;

        List<Cart.CartItem> items = cart.getItems();
        items.removeIf(item -> item.getProductId().equals(productId));

        updateTotalPrice(cart);
        cartRepository.save(cart);
    }


    private void updateTotalPrice(Cart cart) {
        double total = 0;
        for (Cart.CartItem item : cart.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        cart.setTotalPrice(total);
    }

}