package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.*;
import com.quickcart.ecommerce.repository.OrderRepository;
import com.quickcart.ecommerce.repository.UserRepository;
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
            throw new RuntimeException("User not found!");
        }

        Cart cart = cartService.getCartByUserId(userId).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("Pending"); // Initial status
        order.setOrderProducts(new ArrayList<>());

        double totalAmount = 0.0;
        for (CartItem item : cart.getItems()) {
            order.getOrderProducts().add(item.getProduct());
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        saveOrder(order);

        // Clear the cart after placing the order
        cart.getItems().clear();
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

    // New method to update order status after successful payment
    public void updateOrderStatusToPaid(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus("Paid");
            orderRepository.save(order);
        }
    }

    // Method to update order status to any status
    public void updateOrderStatus(String orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}