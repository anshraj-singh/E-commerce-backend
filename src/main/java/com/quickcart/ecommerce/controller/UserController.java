package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.dto.ErrorResponse;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.EmailService;
import com.quickcart.ecommerce.service.UserService;
import com.quickcart.ecommerce.utills.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "Authentication & User Management", description = "APIs for user registration, login, profile management")
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

    @Operation(
            summary = "Register new user",
            description = "Create a new user account. This is a public endpoint. " +
                    "User will receive a confirmation email after successful registration.",
            tags = {"Authentication & User Management"}
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserEntry.class),
                            examples = @ExampleObject(
                                    name = "User Registration",
                                    value = "{\n" +
                                            "  \"username\": \"johndoe\",\n" +
                                            "  \"password\": \"SecurePass123!\",\n" +
                                            "  \"email\": \"john.doe@example.com\",\n" +
                                            "  \"phoneNumber\": \"+91-9876543210\",\n" +
                                            "  \"address\": \"123 Main Street, Mumbai\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UserEntry newUser) {
        try {
            userService.saveUser(newUser);
            String subject = "Welcome to QuickCart!";
            String body = "Dear " + newUser.getUsername() + ",\n\nThank you for registering with QuickCart!";
            emailService.sendEmail(newUser.getEmail(), subject, body);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error during signup", e);
            return new ResponseEntity<>("User registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user and receive JWT token. This is a public endpoint. " +
                    "Use the returned JWT token for accessing protected endpoints.",
            tags = {"Authentication & User Management"}
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserEntry.class),
                            examples = @ExampleObject(
                                    name = "Login Request",
                                    value = "{\n" +
                                            "  \"username\": \"johndoe\",\n" +
                                            "  \"password\": \"SecurePass123!\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UserEntry newUser) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(newUser.getUsername(), newUser.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            log.error("Invalid login attempt", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the profile of currently authenticated user. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            tags = {"Authentication & User Management"}
    )
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry user = userService.findByUsername(username).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Update user credentials",
            description = "Update username, password, or email of authenticated user. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            tags = {"Authentication & User Management"}
    )
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user credentials (provide only fields to update)",
                    content = @Content(
                            schema = @Schema(implementation = UserEntry.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"username\": \"newusername\",\n" +
                                            "  \"password\": \"NewSecurePass123!\",\n" +
                                            "  \"email\": \"newemail@example.com\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UserEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry userInDb = userService.findByUsername(username).orElse(null);
        if (userInDb != null) {
            if (newEntry.getUsername() != null && !newEntry.getUsername().isEmpty()) {
                userInDb.setUsername(newEntry.getUsername());
            }
            if (newEntry.getPassword() != null && !newEntry.getPassword().isEmpty()) {
                userInDb.setPassword(newEntry.getPassword());
            }
            if (newEntry.getEmail() != null && !newEntry.getEmail().isEmpty()) {
                userInDb.setEmail(newEntry.getEmail());
            }
            userService.saveUser(userInDb);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Update user profile",
            description = "Update address and phone number of authenticated user. Requires JWT authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            tags = {"Authentication & User Management"}
    )
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated profile information",
                    content = @Content(
                            schema = @Schema(implementation = UserEntry.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"address\": \"456 New Street, Delhi\",\n" +
                                            "  \"phoneNumber\": \"+91-9988776655\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UserEntry updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntry userInDb = userService.findByUsername(username).orElse(null);
        if (userInDb != null) {
            if (updatedUser.getAddress() != null && !updatedUser.getAddress().isEmpty()) {
                userInDb.setAddress(updatedUser.getAddress());
            }
            if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().isEmpty()) {
                userInDb.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            userService.saveUser(userInDb);
            return new ResponseEntity<>(userInDb, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}