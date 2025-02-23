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

