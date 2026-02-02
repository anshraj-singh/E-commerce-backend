package com.quickcart.ecommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcart.ecommerce.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    @Autowired
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/call")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        // Verify webhook signature
        try {
            Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info("Webhook signature verified successfully");
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Parse the JSON payload manually
        try {
            JsonNode eventJson = objectMapper.readTree(payload);
            String eventType = eventJson.get("type").asText();

            log.info("Received webhook event: {}", eventType);

            // Handle different event types
            if (eventType.equals("checkout.session.completed")) {
                handleCheckoutSessionCompleted(eventJson);
            } else if (eventType.equals("checkout.session.async_payment_succeeded")) {
                handleAsyncPaymentSucceeded(eventJson);
            } else if (eventType.equals("checkout.session.async_payment_failed")) {
                handleAsyncPaymentFailed(eventJson);
            } else if (eventType.equals("payment_intent.succeeded")) {
                log.info("Payment succeeded for event");
            } else if (eventType.equals("payment_intent.payment_failed")) {
                log.warn("Payment failed for event");
            } else {
                log.info("Unhandled event type: {}", eventType);
            }

            return ResponseEntity.ok("Webhook received successfully");

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook");
        }
    }

    private void handleCheckoutSessionCompleted(JsonNode eventJson) {
        try {
            JsonNode sessionData = eventJson.get("data").get("object");
            String sessionId = sessionData.get("id").asText();
            String paymentStatus = sessionData.get("payment_status").asText();

            log.info("Checkout session completed: {}", sessionId);
            log.info("Payment status: {}", paymentStatus);

            // Extract metadata
            JsonNode metadata = sessionData.get("metadata");

            if (metadata != null && !metadata.isNull()) {
                String orderId = metadata.has("orderId") ? metadata.get("orderId").asText() : null;
                String userId = metadata.has("userId") ? metadata.get("userId").asText() : null;

                log.info("Order ID from metadata: {}", orderId);
                log.info("User ID from metadata: {}", userId);

                if (orderId != null && !orderId.isEmpty()) {
                    if ("paid".equals(paymentStatus)) {
                        log.info("Updating order {} to Paid status", orderId);
                        orderService.updateOrderStatusToPaid(orderId);
                        log.info("Order {} status updated to PAID successfully", orderId);
                    } else {
                        log.warn("Payment not completed yet for order {}, status: {}",
                                orderId, paymentStatus);
                    }
                } else {
                    log.error("No orderId found in session metadata");
                }
            } else {
                log.error("Session metadata is null or empty");
            }

        } catch (Exception e) {
            log.error("Error processing checkout.session.completed: {}", e.getMessage(), e);
        }
    }

    private void handleAsyncPaymentSucceeded(JsonNode eventJson) {
        try {
            JsonNode sessionData = eventJson.get("data").get("object");
            JsonNode metadata = sessionData.get("metadata");

            if (metadata != null && !metadata.isNull()) {
                String orderId = metadata.has("orderId") ? metadata.get("orderId").asText() : null;

                if (orderId != null && !orderId.isEmpty()) {
                    log.info("Async payment succeeded for order {}", orderId);
                    orderService.updateOrderStatusToPaid(orderId);
                    log.info("Order {} status updated to Paid (async)", orderId);
                }
            }

        } catch (Exception e) {
            log.error("Error processing async payment succeeded: {}", e.getMessage(), e);
        }
    }

    private void handleAsyncPaymentFailed(JsonNode eventJson) {
        try {
            JsonNode sessionData = eventJson.get("data").get("object");
            JsonNode metadata = sessionData.get("metadata");

            if (metadata != null && !metadata.isNull()) {
                String orderId = metadata.has("orderId") ? metadata.get("orderId").asText() : null;

                if (orderId != null && !orderId.isEmpty()) {
                    log.warn("❌ Async payment failed for order {}", orderId);
                    orderService.updateOrderStatus(orderId, "Payment Failed");
                    log.info("⚠️  Order {} status updated to Payment Failed", orderId);
                }
            }

        } catch (Exception e) {
            log.error("❌ Error processing async payment failed: {}", e.getMessage(), e);
        }
    }
}