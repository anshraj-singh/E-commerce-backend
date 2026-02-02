package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.*;
import com.quickcart.ecommerce.service.*;
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

    @Autowired
    private PaymentService paymentService;

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
            // Create order first
            Order order = orderService.placeOrderFromCart(user.getId());
            double totalAmount = order.getTotalAmount();

            // Convert INR to USD
            double conversionRate = 87.50;
            double amountInUSD = totalAmount / conversionRate;

            // Create a ProductRequest object
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName("Order #" + order.getId());
            productRequest.setAmount((long) (amountInUSD * 100)); // Amount in cents
            productRequest.setCurrency("usd");
            productRequest.setQuantity(1L);

            // Create payment session with orderId in metadata
            StripeResponse stripeResponse = paymentService.checkoutProducts(productRequest, order.getId());

            if ("SUCCESS".equals(stripeResponse.getStatus())) {
                return new ResponseEntity<>(stripeResponse.getSessionUrl(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(stripeResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // User can place a single order for a product
    @PostMapping("/placeSingleOrder/{productId}/{quantity}")
    public ResponseEntity<?> placeSingleOrder(@PathVariable String productId, @PathVariable int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // Get the product
            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
            }

            Product product = productOpt.get();

            // Check stock
            if (quantity > product.getStock()) {
                return new ResponseEntity<>("Not enough stock available!", HttpStatus.BAD_REQUEST);
            }

            // Create order
            Order order = new Order();
            order.setUserId(user.getId());
            order.setStatus("Pending");
            order.setOrderProducts(List.of(product));
            order.setTotalAmount(product.getPrice() * quantity);
            orderService.saveOrder(order);

            // Update stock
            product.setStock(product.getStock() - quantity);
            productService.saveProduct(product);

            // Convert INR to USD
            double conversionRate = 87.50;
            double amountInUSD = order.getTotalAmount() / conversionRate;

            // Create payment request
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName("Order #" + order.getId() + " - " + product.getName());
            productRequest.setAmount((long) (amountInUSD * 100));
            productRequest.setCurrency("usd");
            productRequest.setQuantity((long) quantity);

            // Create payment session with orderId
            StripeResponse stripeResponse = paymentService.checkoutProducts(productRequest, order.getId());

            if ("SUCCESS".equals(stripeResponse.getStatus())) {
                return new ResponseEntity<>(stripeResponse.getSessionUrl(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(stripeResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
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