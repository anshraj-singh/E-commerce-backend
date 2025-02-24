package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.repository.OrderRepository;
import com.quickcart.ecommerce.repository.ProductRepository;
import com.quickcart.ecommerce.repository.UserRepository;
import com.quickcart.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

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


    public Order placeOrderForProduct(String userId, String productId) {
        UserEntry user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        Cart cart = cartService.getCartByUserId(userId).orElse(null);
        if (cart == null || cart.getProductToCart().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }


        Optional<Product> productOptional = cart.getProductToCart().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found in cart!");
        }

        Product product = productOptional.get();


        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("Pending");
        order.setOrderProducts(List.of(product));
        order.setTotalAmount(product.getPrice());

        saveOrder(order);

        cart.setProductToCart(cart.getProductToCart().stream()
                .filter(p -> !p.getId().equals(productId))
                .collect(Collectors.toList()));

        cart.setTotalPrice(cart.getProductToCart().stream().mapToDouble(Product::getPrice).sum());
        cartService.saveCart(cart);

        return order;
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

