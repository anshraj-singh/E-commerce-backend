package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.UserEntry;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser (@RequestBody UserEntry newEntry) {
        try {
            userService.saveUser(newEntry);
            return new ResponseEntity<>(newEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("User  not found", HttpStatus.NOT_FOUND);
    }

    //! update user by basic auth itself
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser (@RequestBody UserEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry userInDb = userService.findByUsername(username).orElse(null);

        if (userInDb != null) {
            if (newEntry.getUsername() != null && !newEntry.getUsername().isEmpty()) {
                userInDb.setUsername(newEntry.getUsername());
            }
            if (newEntry.getPassword() != null && !newEntry.getPassword().isEmpty()) {
                userInDb.setPassword(newEntry.getPassword()); // Consider hashing the password here
            }
            if (newEntry.getEmail() != null && !newEntry.getEmail().isEmpty()) {
                userInDb.setEmail(newEntry.getEmail());
            }
            userService.saveUser(userInDb);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}