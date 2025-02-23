package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void saveOrder(Order order) {
        orderRepository.save(order);
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