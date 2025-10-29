package com.transport.urbain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FallbackController}.
 * <p>
 * Tests verify that all fallback endpoints return proper error responses
 * when services are unavailable.
 */
@DisplayName("Fallback Controller Tests")
class FallbackControllerTest {

    private FallbackController fallbackController;

    @BeforeEach
    void setUp() {
        fallbackController = new FallbackController();
    }

    @Test
    @DisplayName("Should return service unavailable response for user service")
    void testUserServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.userServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(503, body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertTrue(body.get("message").toString().contains("User Service"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should return service unavailable response for ticket service")
    void testTicketServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.ticketServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains("Ticket Service"));
    }

    @Test
    @DisplayName("Should return service unavailable response for schedule service")
    void testScheduleServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.scheduleServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains("Schedule Service"));
    }

    @Test
    @DisplayName("Should return service unavailable response for geolocation service")
    void testGeolocationServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.geolocationServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains("Geolocation Service"));
    }

    @Test
    @DisplayName("Should return service unavailable response for subscription service")
    void testSubscriptionServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.subscriptionServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains("Subscription Service"));
    }

    @Test
    @DisplayName("Should return service unavailable response for notification service")
    void testNotificationServiceFallback() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.notificationServiceFallback();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains("Notification Service"));
    }

    @Test
    @DisplayName("Should include timestamp in fallback response")
    void testFallbackResponseContainsTimestamp() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.userServiceFallback();

        // Then
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        
        // Verify timestamp is of correct type
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
    }

    @Test
    @DisplayName("Should have consistent error response structure")
    void testFallbackResponseStructure() {
        // When
        ResponseEntity<Map<String, Object>> response = fallbackController.userServiceFallback();

        // Then
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        // Verify all expected fields are present
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        
        // Verify field values are correct types
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertTrue(body.get("status") instanceof Integer);
        assertTrue(body.get("error") instanceof String);
        assertTrue(body.get("message") instanceof String);
    }
}

