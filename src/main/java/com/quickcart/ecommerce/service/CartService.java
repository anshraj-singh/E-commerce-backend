package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.CartItem;
import com.quickcart.ecommerce.entity.Product;
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

    // Add product to user's cart
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

        // Decrement the stock of the product
        product.setStock(product.getStock() - quantity);
        // Save the updated product
        productService.saveProduct(product);

        return cartRepository.save(cart);
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