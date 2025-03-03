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
            Order order = orderService.placeOrderFromCart(user.getId());
            double totalAmount = order.getTotalAmount(); // Get the total amount from the order

            // Assuming totalAmount is in INR
            double conversionRate = 87.50; // Example conversion rate: 1 USD = 87.50 INR
            double amountInUSD = totalAmount / conversionRate; // Convert INR to USD

            // Create a ProductRequest object to pass to the PaymentService
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName("Order for " + order.getId());
            productRequest.setAmount((long) (amountInUSD * 100)); // Amount in cents
            productRequest.setCurrency("usd");
            productRequest.setQuantity(1L); // Assuming one item for simplicity

            // Call the payment service to create a payment session
            StripeResponse stripeResponse = paymentService.checkoutProducts(productRequest);

            if ("SUCCESS".equals(stripeResponse.getStatus())) {
                return new ResponseEntity<>(stripeResponse.getSessionUrl(), HttpStatus.CREATED); // Return the payment link
            } else {
                return new ResponseEntity<>(stripeResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed!", HttpStatus.BAD_REQUEST);
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
            // Get the product from the product service
            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
            }

            Product product = productOpt.get();

            // Check if the requested quantity is available in stock
            if (quantity > product.getStock()) {
                return new ResponseEntity<>("Not enough stock available!", HttpStatus.BAD_REQUEST);
            }

            // Create a new order for the single product
            Order order = new Order();
            order.setUserId(user.getId());
            order.setStatus("Pending");
            order.setOrderProducts(List.of(product)); // Add the single product
            order.setTotalAmount(product.getPrice() * quantity); // Set the total amount based on quantity

            orderService.saveOrder(order); // Save the order

            // Update the product stock
            product.setStock(product.getStock() - quantity);
            productService.saveProduct(product); // Save the updated product

            // Clear the cart if needed
            Cart cart = cartService.getCartByUserId(user.getId()).orElse(null);
            if (cart != null) {
                cart.getItems().clear(); // Clear the cart items
                cart.setTotalPrice(0.0); // Reset total price
                cartService.saveCart(cart); // Save the updated cart
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
