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


## Order Management

### Order Entity
The `Order` entity represents order details, including:
- `userId`: The ID of the user who placed the order.
- `items`: A list of products in the order, represented as `OrderItem`.
- `status`: The current status of the order (e.g., "Pending", "Shipped", "Delivered", "Cancelled").
- `totalAmount`: The total amount for the order.

### Order APIs
| HTTP Method | Endpoint               | Description                       |
|-------------|------------------------|-----------------------------------|
| `GET`       | `/order/getAllOrders`  | Retrieve all orders.              |
| `POST`      | `/order/createOrder`   | Create a new order.               |
| `GET`       | `/order/id/{orderId}`  | Retrieve an order by ID.          |
| `DELETE`    | `/order/id/{orderId}`  | Delete an order by ID.            |

## Cart Management

### Cart Entity
The `Cart` entity manages items added by users, including:
- `userId`: The ID of the user who owns the cart.
- `items`: A list of items in the cart, represented as `CartItem`.
- `totalPrice`: The total price of items in the cart.

### Cart APIs
| HTTP Method | Endpoint                     | Description                       |
|-------------|------------------------------|-----------------------------------|
| `GET`       | `/cart/user/{userId}`       | Retrieve the cart for a user.     |
| `POST`      | `/cart/user/{userId}/addItem` | Add an item to the user's cart.   |
| `DELETE`    | `/cart/user/{userId}/removeItem/{productId}` | Remove an item from the user's cart. |

# 🛒 Cart Update System with User Integration

## ✅ Cart System Enhancements

The cart system has been significantly enhanced to improve user experience and functionality. Below are the key features and updates:

### Cart Integration with User

- **User Association**: The cart is now linked to the user using `@DBRef`, ensuring that each user has a unique cart associated with their profile.

### Product Management

- **Add Product to Cart**:
    - Users can easily add products to their cart using product IDs.
    - The cart is automatically associated with the logged-in user, ensuring a seamless shopping experience.

- **Remove Product from Cart**:
    - Users have the ability to remove specific products from their cart, allowing for better management of their selected items.

### Cart Viewing

- **View Cart**:
    - Users can retrieve the details of their cart at any time, providing visibility into the products they intend to purchase.

### Order Placement

- **Place Order from Cart**:
    - When an order is placed, the items in the cart are converted into an order.
    - After the order is confirmed, the cart is automatically cleared, ensuring that users start with a fresh cart for their next shopping session.

### Real-Time Cart Updates

- **Real-Time Data Updates**:
    - The cart data updates in real-time within the user's profile, providing immediate feedback on changes made to the cart.

## 📌 Updated Cart Functionality

The following functionalities have been implemented to enhance the cart experience:

1. **Add Product to Cart**:
    - Users can add products using product IDs, ensuring a quick and efficient way to manage their selections.

2. **Remove Product from Cart**:
    - Users can remove specific products from their cart, allowing for flexibility in their shopping choices.

3. **View Cart**:
    - Users can view their cart details, including product names, quantities, and total price.

4. **Place Order from Cart**:
    - Users can place an order directly from their cart, converting selected items into an order.
    - The cart is cleared automatically after the order is confirmed, streamlining the purchasing process.

## Conclusion

These enhancements to the cart system provide users with a more integrated and user-friendly shopping experience. The ability to manage cart items effectively, view cart details, and place orders seamlessly contributes to a more satisfying e-commerce experience.


##  **Updated Order System**
###  **New Features in Order System**
- **Order Placement Using Product ID**:
    - Users can place an order for a **specific product** using its **Product ID**.
    - The product **must be in the user's cart**; otherwise, the order will not be placed.
    - Once the order is placed, the **product is removed from the cart**, and the **total price is updated**.

### 🔗 **API Endpoints for Order System**
####  **Place an Order (New Feature)**
```http
POST /order/placeOrder/{userId}/{productId}
Request Params:
{userId} → The ID of the user placing the order.
{productId} → The ID of the product to be ordered.
Response:
201 Created if the order is successfully placed.
400 Bad Request if the product is not found in the user's cart.

```
####  Get All Orders
```http
GET /order/getAllOrders
Response: Returns a list of all orders.
```

####  Get Order by ID
```http
GET /order/id/{orderId}
Response: Returns the details of a specific order.
```

####  Delete Order by ID
```http
DELETE /order/id/{orderId}
Response: Deletes the order and returns 204 No Content.
```

# Authentication and Authorization with Spring Security

This project implements user authentication and authorization using Spring Security. The following features are included:

1. **User  Registration**: Users can create accounts via the `/user/createUser ` endpoint, which is publicly accessible.

2. **User  Authentication**: Basic authentication is enabled, allowing users to log in and access their information.

3. **Role-Based Access Control**:
    - Admins can access endpoints under `/admin/**` (e.g., retrieving and deleting users).
    - Users can access their own information via `/user/me` and update their details at `/user/update-user`.

4. **Password Security**: User passwords are securely hashed using BCrypt before being stored in the database.

5. **Stateless Session Management**: The application is configured to use stateless session management, enhancing security.

### Configuration

- **Spring Security Configuration**: The `SpringSecurityConfig` class configures HTTP security, user authentication, and password encoding.
- **Custom User Details Service**: The `CustomeUser DetailsService` retrieves user details for authentication.

### Usage

To test the authentication and authorization features, use tools like Postman to send requests to the specified endpoints, ensuring to include authentication headers where required.

This setup provides a robust foundation for managing user access and securing your application.


## Recent Updates

### Security Enhancements

1. **User  ID Handling**:
    - Removed the need to pass user IDs as path variables in the `CartController` and `OrderController`.
    - Instead, the authenticated user's ID is now retrieved from the security context, ensuring that users can only access their own cart and orders.

2. **Controller Updates**:
    - Modified methods in the `CartController` and `OrderController` to utilize the authenticated user's ID.
    - This change enhances security by preventing unauthorized access to other users' carts and orders.

3. **Security Context Usage**:
    - Implemented the use of the security context to fetch the current user's information in methods that require access to the user's cart or orders.
    - This ensures that all operations are performed in the context of the authenticated user.


# QuickCart E-commerce Application[UPDATE add JWT authentication]

## JWT Authentication
The application uses JWT for user authentication. Upon successful login, a JWT token is generated and must be included in the Authorization header for subsequent requests.

## Overview
QuickCart is an e-commerce application that allows users to register, log in, and manage their accounts. This application implements JWT (JSON Web Token) authentication for secure user sessions.

## Features
- User signup and login
- JWT token generation for authentication
- Secure endpoints for user account management

## Recent Updates

### JWT Authentication
- Implemented JWT for user authentication.
- Added a `JwtUtil` class to handle token generation and validation.
- Integrated a `JwtFilter` to validate tokens on incoming requests.
- Updated `SpringSecurityConfig` to include JWT authentication.

### User Controller Enhancements
- Added `signup` endpoint to allow new users to register.
- Added `login` endpoint to authenticate users and generate JWT tokens upon successful login.

## API Endpoints
- `POST /user/signup`: Register a new user.
- `POST /user/login`: Authenticate a user and receive a JWT token.
- `GET /user/me`: Retrieve the current user's information (requires authentication).
- `PUT /user/update-user`: Update user information (requires authentication).

## E-Commerce Application with Stripe Payment Integration

### Overview
This project is an e-commerce application that integrates Stripe for payment processing. It allows users to place orders and handle payments seamlessly. The key components include the `PaymentService`, `OrderController`, and related classes such as `ProductRequest` and `StripeResponse`.

### Features
- **User  Authentication**: Secure user login and registration.
- **Order Management**: Users can place orders directly from their cart.
- **Stripe Payment Integration**: Create payment sessions using Stripe's API, allowing users to complete transactions securely.
- **Error Handling**: Comprehensive error handling for payment and order placement.

### Stripe Payment Integration
This project integrates Stripe for secure payment processing, allowing users to complete transactions efficiently and safely.

#### Features
- **Secure Payments**: Utilizes Stripe's API for secure transactions.
- **Payment Sessions**: Automatically creates payment sessions for each order, streamlining the checkout process.
- **Error Handling**: Comprehensive error management for payment failures and session creation issues.

#### Usage
- To initiate a payment, send a POST request to /order/placeOrder after the user is authenticated. The request should include the necessary order details.
- Upon successful payment, users will be redirected to a confirmation page.

## Updates to the Cart System

### 1. Cart and CartItem Entities
- **CartItem Class**: Represents an item in the cart, including a reference to the product and its quantity.
- **Cart Class**: Represents the user's cart, which contains a list of `CartItem` objects and calculates the total price based on the items.

### 2. Stock Management
- When a user adds a product to the cart with a specified quantity, the stock of that product is decremented accordingly. This ensures that the available stock is accurately reflected in the system.

### 3. User Cart Association
- The `User Entry` class has been updated to maintain a list of carts associated with the user. This allows for better management of user-specific cart data.

### 4. CartService Enhancements
- The `CartService` class has been modified to handle the addition of products with quantities. It now decrements the stock of the product when it is added to the cart.
- The total price of the cart is calculated based on the quantities of the items in the cart.

### 5. CartController Updates
- The `CartController` has been updated to handle HTTP requests related to adding items to the cart, including checking for stock availability and managing the cart's contents.


## Wishlist Functionality

The Wishlist feature allows users to create and manage a list of products they are interested in purchasing later. This functionality enhances user experience by enabling them to save products for future reference.

### Features

- **Create Wishlist**: Automatically creates a wishlist for users upon their first access.
- **Add Products**: Users can add products to their wishlist.
- **Remove Products**: Users can remove products from their wishlist.
- **View Wishlist**: Users can view all products in their wishlist.

### API Endpoints

- **Get User's Wishlist**
    - `GET /wishlist/me`
    - Retrieves the wishlist for the authenticated user.

- **Add Product to Wishlist**
    - `POST /wishlist/add/{productId}`
    - Adds a specified product to the authenticated user's wishlist.

- **Remove Product from Wishlist**
    - `DELETE /wishlist/remove/{productId}`
    - Removes a specified product from the authenticated user's wishlist.

### Implementation Details

1. **Entity**: A new `Wishlist` entity was created to represent the user's wishlist.
2. **Repository**: A `WishlistRepository` interface was added to handle database operations.
3. **Service**: A `WishlistService` class was implemented to manage business logic related to wishlists.
4. **Controller**: A `WishlistController` class was created to expose the API endpoints for wishlist operations.
5. **Security**: The wishlist endpoints are secured to ensure that only authenticated users can access them.

This feature enhances the overall functionality of the e-commerce platform, providing users with a convenient way to manage their desired products.
