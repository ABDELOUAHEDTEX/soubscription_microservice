package com.transport.urbain.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller for handling service unavailability.
 * <p>
 * This controller provides fallback responses when microservices are unavailable
 * due to circuit breaker activation, service downtime, or network issues.
 * <p>
 * When a circuit breaker opens (services fail repeatedly), requests are
 * automatically forwarded to this controller with a user-friendly error message.
 * <p>
 * Fallback endpoints are triggered by the circuit breaker configuration
 * in GatewayConfig for each service route.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Fallback endpoint for user service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        return createFallbackResponse("User Service is currently unavailable. Please try again later.");
    }

    /**
     * Fallback endpoint for ticket service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/ticket-service")
    public ResponseEntity<Map<String, Object>> ticketServiceFallback() {
        return createFallbackResponse("Ticket Service is currently unavailable. Please try again later.");
    }

    /**
     * Fallback endpoint for schedule service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/schedule-service")
    public ResponseEntity<Map<String, Object>> scheduleServiceFallback() {
        return createFallbackResponse("Schedule Service is currently unavailable. Please try again later.");
    }

    /**
     * Fallback endpoint for geolocation service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/geolocation-service")
    public ResponseEntity<Map<String, Object>> geolocationServiceFallback() {
        return createFallbackResponse("Geolocation Service is currently unavailable. Please try again later.");
    }

    /**
     * Fallback endpoint for subscription service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/subscription-service")
    public ResponseEntity<Map<String, Object>> subscriptionServiceFallback() {
        return createFallbackResponse("Subscription Service is currently unavailable. Please try again later.");
    }

    /**
     * Fallback endpoint for notification service unavailability.
     *
     * @return ResponseEntity with 503 Service Unavailable status and error message
     */
    @GetMapping("/notification-service")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        return createFallbackResponse("Notification Service is currently unavailable. Please try again later.");
    }

    /**
     * Helper method to create a standardized fallback response.
     * <p>
     * Creates a consistent error response format with timestamp, status code,
     * error type, and descriptive message.
     *
     * @param message the error message to include in the response
     * @return ResponseEntity with 503 Service Unavailable status and error details
     */
    private ResponseEntity<Map<String, Object>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
