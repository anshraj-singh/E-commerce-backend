package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.EmailService;
import com.quickcart.ecommerce.service.UserService;
import com.quickcart.ecommerce.utills.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserEntry newUser ) {
        try {
            userService.saveUser(newUser);

            // Send confirmation email
            String subject = "Welcome to QuickCart!";
            String body = "Dear " + newUser .getUsername() + ",\n\nThank you for registering with QuickCart!";
            emailService.sendEmail(newUser.getEmail(), subject, body);

            return new ResponseEntity<>("User  registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error during signup", e);
            return new ResponseEntity<>("User  registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntry newUser ) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(newUser .getUsername(), newUser .getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(newUser .getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            log.error("Invalid login attempt", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
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