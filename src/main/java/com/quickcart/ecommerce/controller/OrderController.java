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


    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        if (!allOrders.isEmpty()) {
            return new ResponseEntity<>(allOrders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    //! user can order all products in cart
    @PostMapping("/placeOrder/{userId}")
    public ResponseEntity<?> placeOrder(@PathVariable String userId) {
        try {
            Order order = orderService.placeOrderFromCart(userId);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed!", HttpStatus.BAD_REQUEST);
        }
    }

    //! user can one by one oder in cart api
    @PostMapping("/placeSingleOrder/{userId}/{productId}")
    public ResponseEntity<?> placeSingleOrder(@PathVariable String userId, @PathVariable String productId) {
        try {
            // Get the product from the product service
            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
            }

            // Create a new order for the single product
            Order order = new Order();
            order.setUserId(userId);
            order.setStatus("Pending");
            order.setOrderProducts(List.of(productOpt.get())); // Add the single product
            order.setTotalAmount(productOpt.get().getPrice()); // Set the total amount

            orderService.saveOrder(order); // Save the order

            Cart cart = cartService.getCartByUserId(userId).orElse(null);
            cart.getProductToCart().clear();
            cart.setTotalPrice(0.0);
            cartService.saveCart(cart);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Order placement failed!", HttpStatus.BAD_REQUEST);
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
