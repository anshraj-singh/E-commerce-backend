package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.repository.CartRepository;
import com.quickcart.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(String userId) {
        return Optional.ofNullable(cartRepository.findByUserId(userId));
    }

    // Add product to user's cart
    public Cart addProductToCart(String userId, Product product) {
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }

        // Add product to cart
        cart.getProductToCart().add(product);
        updateTotalPrice(cart);
        cart = cartRepository.save(cart);

        // Update user's cart reference
        Optional<UserEntry> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntry user = userOpt.get();
            if (!user.getCarts().contains(cart)) {
                user.getCarts().add(cart);
                userRepository.save(user);
            }
        }

        return cart;
    }

    // Remove product from cart
    public void removeProductFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getProductToCart().removeIf(product -> product.getId().equals(productId));
            updateTotalPrice(cart);
            cartRepository.save(cart);
        }
    }

    // Update total price of the cart
    private void updateTotalPrice(Cart cart) {
        double total = cart.getProductToCart().stream().mapToDouble(Product::getPrice).sum();
        cart.setTotalPrice(total);
    }
}

