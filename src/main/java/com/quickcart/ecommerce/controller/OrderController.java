package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.CartService;
import com.quickcart.ecommerce.service.OrderService;
import com.quickcart.ecommerce.service.ProductService;
import com.quickcart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    // Get all orders for the authenticated user
    @GetMapping("/me")
    public ResponseEntity<List<Order>> getAllOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Order> allOrders = orderService.getAllOrdersByUserId(user.getId());
        return new ResponseEntity<>(allOrders, HttpStatus.OK);
    }

    // Place an order from the user's cart
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Order order = orderService.placeOrderFromCart(user.getId());
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed!", HttpStatus.BAD_REQUEST);
        }
    }

    // User can place a single order for a product
    @PostMapping("/placeSingleOrder/{productId}")
    public ResponseEntity<?> placeSingleOrder(@PathVariable String productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // Get the product from the product service
            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
            }

            // Create a new order for the single product
            Order order = new Order();
            order.setUserId(user.getId());
            order.setStatus("Pending");
            order.setOrderProducts(List.of(productOpt.get())); // Add the single product
            order.setTotalAmount(productOpt.get().getPrice()); // Set the total amount

            orderService.saveOrder(order); // Save the order

            Cart cart = cartService.getCartByUserId(user.getId()).orElse(null);
            if (cart != null) {
                cart.getProductToCart().clear();
                cart.setTotalPrice(0.0);
                cartService.saveCart(cart);
            }
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed!", HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/id/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Order> order = orderService.getById(orderId);
        if (order.isPresent() && order.get().getUserId().equals(user.getId())) {
            return new ResponseEntity<>(order.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{orderId}")
    public ResponseEntity<?> deleteOrderById(@PathVariable String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Order> order = orderService.getById(orderId);
        if (order.isPresent() && order.get().getUserId().equals(user.getId())) {
            orderService.deleteById(orderId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
