package com.transport.urbain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Schedule Service.
 * <p>
 * This class handles all exceptions thrown by controllers and converts them
 * to appropriate HTTP responses. It provides consistent error handling across
 * all endpoints, logging errors and returning standardized error responses.
 * <p>
 * Handles:
 * <ul>
 *     <li>Not Found exceptions (routes, stops, schedules, buses)</li>
 *     <li>Duplicate entity exceptions</li>
 *     <li>Invalid schedule exceptions</li>
 *     <li>Validation errors</li>
 *     <li>Unexpected errors</li>
 * </ul>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles RouteNotFoundException.
     * Returns HTTP 404 Not Found response.
     *
     * @param ex the route not found exception
     * @return error response with 404 status
     */
    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRouteNotFoundException(RouteNotFoundException ex) {
        log.error("Route not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Route Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles StopNotFoundException.
     * Returns HTTP 404 Not Found response.
     *
     * @param ex the stop not found exception
     * @return error response with 404 status
     */
    @ExceptionHandler(StopNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStopNotFoundException(StopNotFoundException ex) {
        log.error("Stop not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Stop Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles ScheduleNotFoundException.
     * Returns HTTP 404 Not Found response.
     *
     * @param ex the schedule not found exception
     * @return error response with 404 status
     */
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScheduleNotFoundException(ScheduleNotFoundException ex) {
        log.error("Schedule not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Schedule Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles BusNotFoundException.
     * Returns HTTP 404 Not Found response.
     *
     * @param ex the bus not found exception
     * @return error response with 404 status
     */
    @ExceptionHandler(BusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBusNotFoundException(BusNotFoundException ex) {
        log.error("Bus not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Bus Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles DuplicateRouteException.
     * Returns HTTP 409 Conflict response.
     *
     * @param ex the duplicate route exception
     * @return error response with 409 status
     */
    @ExceptionHandler(DuplicateRouteException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRouteException(DuplicateRouteException ex) {
        log.error("Duplicate route: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Duplicate Route",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles DuplicateStopException.
     * Returns HTTP 409 Conflict response.
     *
     * @param ex the duplicate stop exception
     * @return error response with 409 status
     */
    @ExceptionHandler(DuplicateStopException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStopException(DuplicateStopException ex) {
        log.error("Duplicate stop: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Duplicate Stop",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles DuplicateBusException.
     * Returns HTTP 409 Conflict response.
     *
     * @param ex the duplicate bus exception
     * @return error response with 409 status
     */
    @ExceptionHandler(DuplicateBusException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBusException(DuplicateBusException ex) {
        log.error("Duplicate bus: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Duplicate Bus",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles InvalidScheduleException.
     * Returns HTTP 400 Bad Request response.
     *
     * @param ex the invalid schedule exception
     * @return error response with 400 status
     */
    @ExceptionHandler(InvalidScheduleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidScheduleException(InvalidScheduleException ex) {
        log.error("Invalid schedule: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Schedule",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles validation errors from request DTOs.
     * Returns HTTP 400 Bad Request with field-specific error messages.
     *
     * @param ex the validation exception
     * @return error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles all unexpected exceptions.
     * Returns HTTP 500 Internal Server Error response.
     *
     * @param ex the unexpected exception
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Standard error response structure.
     * <p>
     * Contains timestamp, HTTP status code, error type, and error message
     * for consistent error responses across all endpoints.
     *
     * @param timestamp when the error occurred
     * @param status HTTP status code
     * @param error error type/category
     * @param message detailed error message
     */
    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message
    ) {}
}