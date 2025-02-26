package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.repository.OrderRepository;
import com.quickcart.ecommerce.repository.UserRepository;
import com.quickcart.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;


    public void saveOrder(Order order) {
        orderRepository.save(order);
        UserEntry user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            user.getOrders().add(order);
            userRepository.save(user);
        }
    }


    public Order placeOrderFromCart(String userId) {
        UserEntry user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User  not found!");
        }

        Cart cart = cartService.getCartByUserId(userId).orElse(null);
        if (cart == null || cart.getProductToCart().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("Pending");
        order.setOrderProducts(new ArrayList<>(cart.getProductToCart())); // Ensure products are added correctly
        order.setTotalAmount(cart.getTotalPrice());

        saveOrder(order);
        userService.addOrderToUser(userId, order); // Use the addOrderToUser  method

        cart.getProductToCart().clear();
        cart.setTotalPrice(0.0);
        cartService.saveCart(cart);

        return order;
    }

    public List<Order> getAllOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    public Optional<Order> getById(String orderId) {
        return orderRepository.findById(orderId);
    }


    public void deleteById(String orderId) {
        orderRepository.deleteById(orderId);
    }
}
