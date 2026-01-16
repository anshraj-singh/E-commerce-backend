package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.Product;
import com.quickcart.ecommerce.entity.UserEntry;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntry>> getAllUsers() {
        List<UserEntry> all = userService.getAllUser ();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String myId) {
        userService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createUser(@RequestBody UserEntry admin){
        try {
            userService.saveAdmin(admin);
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
