package com.quickcart.ecommerce.controller;

import com.quickcart.ecommerce.dto.ErrorResponse;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin - User Management", description = "Admin-only APIs for user management. Requires ADMIN role.")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "[ADMIN] Get all users",
            description = "Retrieve list of all registered users. Restricted to ADMIN role only. " +
                    "Requires JWT token with ADMIN privileges.",
            tags = {"Admin - User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEntry.class, type = "array")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No users found"
            )
    })
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntry>> getAllUsers() {
        List<UserEntry> all = userService.getAllUser();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "[ADMIN] Delete user by ID",
            description = "Permanently delete a user account. Restricted to ADMIN role only. " +
                    "This action cannot be undone. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteUserById(
            @Parameter(description = "User ID to delete", required = true, example = "65abc123def456789012")
            @PathVariable String myId) {
        userService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "[ADMIN] Create admin user",
            description = "Create a new user account with ADMIN role. Restricted to ADMIN role only. " +
                    "Admin users have access to all admin endpoints. Requires JWT token with ADMIN privileges.",
            tags = {"Admin - User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Admin user created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEntry.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data or username already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Admin user details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserEntry.class),
                            examples = @ExampleObject(
                                    name = "Create Admin",
                                    value = "{\n" +
                                            "  \"username\": \"admin\",\n" +
                                            "  \"password\": \"AdminPass123!\",\n" +
                                            "  \"email\": \"admin@quickcart.com\",\n" +
                                            "  \"phoneNumber\": \"+91-9876543210\",\n" +
                                            "  \"address\": \"Admin Office, Mumbai\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UserEntry admin) {
        try {
            userService.saveAdmin(admin);
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}