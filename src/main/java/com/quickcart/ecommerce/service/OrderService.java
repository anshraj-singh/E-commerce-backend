package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.*;
import com.quickcart.ecommerce.repository.OrderRepository;
import com.quickcart.ecommerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

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

        //STEP 1: Validate stock availability BEFORE creating order
        for (CartItem item : cart.getItems()) {
            Product product = productService.getById(item.getProduct().getId()).orElse(null);
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getProduct().getName());
            }
            if (item.getQuantity() > product.getStock()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStock() + ", Requested: " + item.getQuantity());
            }
        }

        //STEP 2: Create order with "Pending" status and OrderItems
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("Pending");
        order.setOrderItems(new ArrayList<>());

        double totalAmount = 0.0;
        for (CartItem cartItem : cart.getItems()) {
            Product product = productService.getById(cartItem.getProduct().getId()).orElse(null);

            // Create OrderItem with product, quantity, and price
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrder(product.getPrice());

            order.getOrderItems().add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        log.info("Order created with ID: {} and status: Pending (Stock NOT deducted yet)", order.getId());

        saveOrder(order);

        //STEP 3: Clear the cart after order is placed
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

    // CRITICAL: Update order status to "Paid" AND deduct stock from products
    public void updateOrderStatusToPaid(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            log.info("Processing payment completion for order: {}", orderId);
            log.info("Starting stock deduction process...");

            // Deduct stock for each product in the order
            for (OrderItem orderItem : order.getOrderItems()) {
                Optional<Product> productOpt = productService.getById(orderItem.getProduct().getId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    int quantityOrdered = orderItem.getQuantity();

                    // Verify stock is still available
                    if (product.getStock() >= quantityOrdered) {
                        int oldStock = product.getStock();
                        product.setStock(product.getStock() - quantityOrdered);
                        productService.saveProduct(product);

                        log.info("Stock deducted for '{}' | Quantity: {} | Old Stock: {} | New Stock: {}",
                                product.getName(), quantityOrdered, oldStock, product.getStock());
                    } else {
                        log.error("Insufficient stock for product '{}' | Available: {} | Requested: {}",
                                product.getName(), product.getStock(), quantityOrdered);
                        // You might want to handle this case differently
                        // For now, we'll still mark order as paid but log the error
                    }
                }
            }

            // Update order status to "Paid"
            order.setStatus("Paid");
            orderRepository.save(order);

            log.info("Order {} status updated to PAID and stock deducted", orderId);
        }
    }

    // Method to update order status to any status
    public void updateOrderStatus(String orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order);
            log.info("Order {} status updated to: {}", orderId, status);
        }
    }
}