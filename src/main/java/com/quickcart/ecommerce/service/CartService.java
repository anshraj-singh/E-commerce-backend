package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.CartItem;
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

    @Autowired
    private ProductService productService;

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(String userId) {
        return Optional.ofNullable(cartRepository.findByUserId(userId));
    }

    // Add product to user's cart (WITHOUT deducting stock)
    public Cart addProductToCart(String userId, Product product, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }

        // Check if the product is already in the cart
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity); // Update quantity
                updateTotalPrice(cart);
                return cartRepository.save(cart);
            }
        }

        // If the product is not in the cart, add it
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        cart.getItems().add(newItem);
        updateTotalPrice(cart);

        // REMOVED: Stock deduction happens here (WRONG!)
        // Stock will be deducted only when payment is completed

        // Save the cart and update the user's cart list
        Cart savedCart = cartRepository.save(cart);
        updateUserCart(userId, savedCart);
        return savedCart;
    }

    // Update user's cart list
    private void updateUserCart(String userId, Cart cart) {
        UserEntry user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Remove old cart references and add new one
            user.getCarts().removeIf(c -> c.getId().equals(cart.getId()));
            user.getCarts().add(cart);
            userRepository.save(user);
        }
    }

    // Remove product from cart
    public void removeProductFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
            updateTotalPrice(cart);
            cartRepository.save(cart);
        }
    }

    // Update total price of the cart
    private void updateTotalPrice(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}