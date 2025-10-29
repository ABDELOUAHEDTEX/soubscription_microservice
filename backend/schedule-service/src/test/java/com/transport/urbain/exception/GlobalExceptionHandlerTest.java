package com.transport.urbain.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 * <p>
 * This test class covers exception handling logic including:
 * <ul>
 *     <li>Custom exception handling</li>
 *     <li>Validation error handling</li>
 *     <li>Generic exception handling</li>
 *     <li>Error response formatting</li>
 * </ul>
 *
 * @author Transport Team
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    /**
     * Sets up test data before each test method.
     * Creates an instance of GlobalExceptionHandler.
     */
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    /**
     * Tests handling of BusNotFoundException.
     * Verifies that a 404 Not Found response is returned.
     */
    @Test
    void testHandleBusNotFoundException() {
        // Arrange
        BusNotFoundException exception = new BusNotFoundException("Bus not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleBusNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bus not found", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of RouteNotFoundException.
     * Verifies that a 404 Not Found response is returned.
     */
    @Test
    void testHandleRouteNotFoundException() {
        // Arrange
        RouteNotFoundException exception = new RouteNotFoundException("Route not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleRouteNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Route not found", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of ScheduleNotFoundException.
     * Verifies that a 404 Not Found response is returned.
     */
    @Test
    void testHandleScheduleNotFoundException() {
        // Arrange
        ScheduleNotFoundException exception = new ScheduleNotFoundException("Schedule not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleScheduleNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Schedule not found", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of StopNotFoundException.
     * Verifies that a 404 Not Found response is returned.
     */
    @Test
    void testHandleStopNotFoundException() {
        // Arrange
        StopNotFoundException exception = new StopNotFoundException("Stop not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleStopNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Stop not found", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of DuplicateBusException.
     * Verifies that a 409 Conflict response is returned.
     */
    @Test
    void testHandleDuplicateBusException() {
        // Arrange
        DuplicateBusException exception = new DuplicateBusException("Bus already exists");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleDuplicateBusException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bus already exists", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of DuplicateRouteException.
     * Verifies that a 409 Conflict response is returned.
     */
    @Test
    void testHandleDuplicateRouteException() {
        // Arrange
        DuplicateRouteException exception = new DuplicateRouteException("Route already exists");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleDuplicateRouteException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Route already exists", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of DuplicateStopException.
     * Verifies that a 409 Conflict response is returned.
     */
    @Test
    void testHandleDuplicateStopException() {
        // Arrange
        DuplicateStopException exception = new DuplicateStopException("Stop already exists");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleDuplicateStopException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Stop already exists", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of InvalidScheduleException.
     * Verifies that a 400 Bad Request response is returned.
     */
    @Test
    void testHandleInvalidScheduleException() {
        // Arrange
        InvalidScheduleException exception = new InvalidScheduleException("Invalid schedule");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleInvalidScheduleException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid schedule", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of generic Exceptions.
     * Verifies that a 500 Internal Server Error response is returned.
     */
    @Test
    void testHandleGlobalException() {
        // Arrange
        Exception exception = new Exception("Unexpected error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleGlobalException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", Objects.requireNonNull(response.getBody()).message());
    }

    /**
     * Tests handling of validation errors.
     * Verifies that a 400 Bad Request response with validation details is returned.
     */
    @Test
    void testHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Tests that ErrorResponse record is properly structured.
     * Verifies that all required fields are present.
     */
    @Test
    void testErrorResponseStructure() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String message = "Test error message";

        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = 
                new GlobalExceptionHandler.ErrorResponse(
                        timestamp,
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        message
                );

        // Assert
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.message());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals(timestamp, errorResponse.timestamp());
    }
}

