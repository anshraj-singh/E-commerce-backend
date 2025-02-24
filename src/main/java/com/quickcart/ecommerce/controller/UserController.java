package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntry>> getAllUsers() {
        List<UserEntry> all = userService.getAllUser ();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody UserEntry newEntry) {
        try {
            userService.saveEntry(newEntry);
            return new ResponseEntity<>(newEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getUserById(@PathVariable String myId) {
        Optional<UserEntry> entry = userService.getById(myId);
        if (entry.isPresent()) {
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String myId) {
        userService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<UserEntry> updateEntryById(@PathVariable String myId, @RequestBody UserEntry newEntry) {
        UserEntry old = userService.getById(myId).orElse(null);
        if (old != null) {
            if (newEntry.getUsername() != null && !newEntry.getUsername().isEmpty()) {
                old.setUsername(newEntry.getUsername());
            }
            if (newEntry.getPassword() != null && !newEntry.getPassword().isEmpty()) {
                old.setPassword(newEntry.getPassword());
            }
            if (newEntry.getEmail() != null && !newEntry.getEmail().isEmpty()) {
                old.setEmail(newEntry.getEmail());
            }
            userService.saveEntry(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

