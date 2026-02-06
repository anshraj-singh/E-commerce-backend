package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.entity.Wishlist;
import com.quickcart.ecommerce.service.ProductService;
import com.quickcart.ecommerce.service.WishlistService;
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
@RequestMapping("/wishlist")
@Tag(name = "Wishlist", description = "APIs for managing user's wishlist/favorites. Requires authentication (USER role).")
@SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Get user's wishlist",
            description = "Retrieve the wishlist for currently authenticated user. " +
                    "Wishlist data is cached in Redis with 1-hour TTL for better performance. " +
                    "Requires JWT authentication.",
            tags = {"Wishlist"}
    )
    @GetMapping("/me")
    public ResponseEntity<Wishlist> getWishlistByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Wishlist> wishlist = wishlistService.getWishlistByUserId(user.getId());
        return wishlist.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Add product to wishlist",
            description = "Add a product to user's wishlist/favorites. " +
                    "Wishlist cache will be cleared to reflect the update. " +
                    "Requires JWT authentication.",
            tags = {"Wishlist"}
    )
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addProductToWishlist(
            @Parameter(description = "Product ID to add to wishlist", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product product = productService.getById(productId).orElse(null);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        wishlistService.addProductToWishlist(user.getId(), product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Remove product from wishlist",
            description = "Remove a specific product from user's wishlist. " +
                    "Wishlist cache will be cleared to reflect the update. " +
                    "Requires JWT authentication.",
            tags = {"Wishlist"}
    )
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeProductFromWishlist(
            @Parameter(description = "Product ID to remove from wishlist", required = true, example = "65abc123def456789012")
            @PathVariable String productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        wishlistService.removeProductFromWishlist(user.getId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}