package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.service.OrderService;
import com.quickcart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    //! user can get all orders
    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        if (!allOrders.isEmpty()) {
            return new ResponseEntity<>(allOrders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/placeOrder/{userId}/{productId}")
    public ResponseEntity<?> placeOrder(@PathVariable String userId, @PathVariable String productId) {
        try {
            Order order = orderService.placeOrderForProduct(userId, productId);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/id/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        Optional<Order> order = orderService.getById(orderId);
        return order.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("/id/{orderId}")
    public ResponseEntity<?> deleteOrderById(@PathVariable String orderId) {
        orderService.deleteById(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

