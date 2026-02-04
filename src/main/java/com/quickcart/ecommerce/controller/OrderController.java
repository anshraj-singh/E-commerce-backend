package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.dto.ErrorResponse;
import com.quickcart.ecommerce.entity.*;
import com.quickcart.ecommerce.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@Tag(name = "Orders", description = "APIs for order management and checkout. Requires authentication (USER role).")
@SecurityRequirement(name = "Bearer Authentication")
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

    @Operation(
            summary = "Get user's orders",
            description = "Retrieve all orders placed by the currently authenticated user. " +
                    "Returns order history with status, items, and amounts. Requires JWT authentication.",
            tags = {"Orders"}
    )
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

    @Operation(
            summary = "Place order from cart",
            description = "Create order from user's cart and initiate Stripe payment. " +
                    "Order is created with 'Pending' status. Stock is validated but NOT deducted. " +
                    "Returns Stripe checkout URL for payment. After successful payment, " +
                    "order status updates to 'Paid' and stock is deducted via webhook. " +
                    "Cart is cleared after order creation. Requires JWT authentication.",
            tags = {"Orders"}
    )
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
            double totalAmount = order.getTotalAmount();

            double conversionRate = 87.50;
            double amountInUSD = totalAmount / conversionRate;

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName("Order #" + order.getId());
            productRequest.setAmount((long) (amountInUSD * 100));
            productRequest.setCurrency("usd");
            productRequest.setQuantity(1L);

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

    @Operation(
            summary = "Place single product order",
            description = "Create order for a single product with specified quantity and initiate payment. " +
                    "Useful for 'Buy Now' functionality. Order created with 'Pending' status. " +
                    "Stock validated but NOT deducted until payment completion. " +
                    "Returns Stripe checkout URL. Requires JWT authentication.",
            tags = {"Orders"}
    )
    @PostMapping("/placeSingleOrder/{productId}/{quantity}")
    public ResponseEntity<?> placeSingleOrder(
            @Parameter(description = "Product ID to order", required = true, example = "65abc123def456789012")
            @PathVariable String productId,
            @Parameter(description = "Quantity to order", required = true, example = "1")
            @PathVariable int quantity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
            }

            Product product = productOpt.get();

            if (quantity > product.getStock()) {
                return new ResponseEntity<>("Not enough stock available!", HttpStatus.BAD_REQUEST);
            }

            Order order = new Order();
            order.setUserId(user.getId());
            order.setStatus("Pending");
            order.setOrderItems(new ArrayList<>());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtOrder(product.getPrice());

            order.getOrderItems().add(orderItem);
            order.setTotalAmount(product.getPrice() * quantity);
            orderService.saveOrder(order);

            double conversionRate = 87.50;
            double amountInUSD = order.getTotalAmount() / conversionRate;

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName("Order #" + order.getId() + " - " + product.getName());
            productRequest.setAmount((long) (amountInUSD * 100));
            productRequest.setCurrency("usd");
            productRequest.setQuantity((long) quantity);

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

    @Operation(
            summary = "Get order by ID",
            description = "Retrieve details of a specific order. User can only access their own orders. " +
                    "Requires JWT authentication.",
            tags = {"Orders"}
    )
    @GetMapping("/id/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", required = true, example = "65def789ghi012345678")
            @PathVariable String orderId) {

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

    @Operation(
            summary = "Delete order",
            description = "Cancel/delete an order. User can only delete their own orders. " +
                    "Note: Cannot delete orders with 'Paid' status. Requires JWT authentication.",
            tags = {"Orders"}
    )
    @DeleteMapping("/id/{orderId}")
    public ResponseEntity<?> deleteOrderById(
            @Parameter(description = "Order ID to delete", required = true, example = "65def789ghi012345678")
            @PathVariable String orderId) {

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