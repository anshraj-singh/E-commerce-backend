# E-Commerce Backend

## Overview
This is a backend service for an e-commerce platform built using Spring Boot and MongoDB. It provides functionalities for user management, product management, shopping cart, and order processing.

## Features
- User Registration and Authentication (JWT)
- Product Management (CRUD operations)
- Shopping Cart Functionality
- Order Processing
- Integration with Razorpay for payment processing

## API Endpoints

### **User Management**

- `POST /user/createUser`  - Create a new user
- `GET /user/getAllUsers` - Retrieve all users
- `GET /user/id/{myId}` - Retrieve user by ID
- `PUT /user/id/{myId}` - Update user by ID
- `DELETE /user/id/{myId}` - Delete user by ID


### **Product Management**

- `POST /product/createProduct` - Create a new product
- `GET /product/getAllProducts` - Retrieve all products
- `GET /product/id/{productId}` - Retrieve product by ID
- `PUT /product/id/{productId}` - Update product by ID
- `DELETE /product/id/{productId}` - Delete product by ID