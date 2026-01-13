package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.Wishlist;
import com.quickcart.ecommerce.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public void saveWishlist(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }

    @Cacheable(value = "wishlists", key = "#userId")
    public Optional<Wishlist> getWishlistByUserId(String userId) {
        return Optional.ofNullable(wishlistRepository.findByUserId(userId));
    }

    @CacheEvict(value = "wishlists", key = "#userId")
    public void addProductToWishlist(String userId, Product product) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
        }
        wishlist.getProducts().add(product);
        wishlistRepository.save(wishlist);
    }

    @CacheEvict(value = "wishlists", key = "#userId")
    public void removeProductFromWishlist(String userId, String productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist != null) {
            wishlist.getProducts().removeIf(product -> product.getId().equals(productId));
            wishlistRepository.save(wishlist);
        }
    }
}