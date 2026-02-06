package com.quickcart.ecommerce.controller;


import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.CartService;
import com.quickcart.ecommerce.service.ProductService;
import com.quickcart.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cart")
@Tag(name = "Shopping Cart", description = "APIs for managing user's shopping cart. Requires authentication (USER role).")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Get user's cart",
            description = "Retrieve the shopping cart for the currently authenticated user. " +
                    "Returns cart with all items, quantities, and total price. Requires JWT authentication.",
            tags = {"Shopping Cart"}
    )
    @GetMapping("/me")
    public ResponseEntity<Cart> getCartByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Cart> cart = cartService.getCartByUserId(user.getId());
        return cart.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Add product to cart",
            description = "Add a product with specified quantity to user's cart. " +
                    "If product already exists in cart, quantity will be updated. " +
                    "Stock validation is performed but stock is NOT deducted until payment completion. " +
                    "Requires JWT authentication.",
            tags = {"Shopping Cart"}
    )
    @PostMapping("/addItem/{productId}/{quantity}")
    public ResponseEntity<?> addItemToCart(
            @Parameter(description = "Product ID to add", required = true, example = "65abc123def456789012")
            @PathVariable String productId,
            @Parameter(description = "Quantity to add (must be positive integer)", required = true, example = "2")
            @PathVariable int quantity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Product> productOpt = productService.getById(productId);
        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product product = productOpt.get();
        if (quantity > product.getStock()) {
            return new ResponseEntity<>("Insufficient stock. Available: " + product.getStock(),
                    HttpStatus.BAD_REQUEST);
        }

        Cart updatedCart = cartService.addProductToCart(user.getId(), product, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove product from cart",
            description = "Remove a specific product from user's cart completely. " +
                    "Requires JWT authentication.",
            tags = {"Shopping Cart"}
    )
    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<?> removeItemFromCart(
            @Parameter(description = "Product ID to remove", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        cartService.removeProductFromCart(user.getId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}