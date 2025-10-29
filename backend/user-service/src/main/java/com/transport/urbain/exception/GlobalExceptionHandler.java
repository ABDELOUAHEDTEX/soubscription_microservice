package com.transport.urbain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * 
 * <p>This class provides centralized exception handling across all REST controllers.
 * It catches exceptions thrown during request processing and converts them into
 * appropriate HTTP responses with proper status codes and error messages.
 * 
 * <p>Handled exceptions include:
 * <ul>
 *   <li>UserNotFoundException - 404 NOT FOUND</li>
 *   <li>DuplicateUserException - 409 CONFLICT</li>
 *   <li>InvalidCredentialsException - 401 UNAUTHORIZED</li>
 *   <li>TokenExpiredException - 401 UNAUTHORIZED</li>
 *   <li>AccessDeniedException - 403 FORBIDDEN</li>
 *   <li>MethodArgumentNotValidException - 400 BAD REQUEST (validation errors)</li>
 *   <li>Exception - 500 INTERNAL SERVER ERROR (fallback)</li>
 * </ul>
 * 
 * <p>All exceptions are logged with appropriate log levels. Error responses
 * include timestamps, status codes, error types, and detailed messages.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException.
     * 
     * <p>Returns a 404 NOT FOUND response when a requested user cannot be found.
     * 
     * @param ex the UserNotFoundException that was thrown
     * @return ResponseEntity containing error details with 404 status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles DuplicateUserException.
     * 
     * <p>Returns a 409 CONFLICT response when attempting to create a user that
     * already exists.
     * 
     * @param ex the DuplicateUserException that was thrown
     * @return ResponseEntity containing error details with 409 status
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException ex) {
        log.error("Duplicate user: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Duplicate User",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles InvalidCredentialsException.
     * 
     * <p>Returns a 401 UNAUTHORIZED response when authentication fails due to
     * invalid credentials.
     * 
     * @param ex the InvalidCredentialsException that was thrown
     * @return ResponseEntity containing error details with 401 status
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Credentials",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles TokenExpiredException.
     * 
     * <p>Returns a 401 UNAUTHORIZED response when a JWT token is expired or invalid.
     * 
     * @param ex the TokenExpiredException that was thrown
     * @return ResponseEntity containing error details with 401 status
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        log.error("Token expired: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Token Expired",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles Spring Security AccessDeniedException.
     * 
     * <p>Returns a 403 FORBIDDEN response when a user attempts to access a resource
     * they do not have permission to access.
     * 
     * @param ex the AccessDeniedException that was thrown
     * @return ResponseEntity containing error details with 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handles validation exceptions from request body validation.
     * 
     * <p>Returns a 400 BAD REQUEST response when request validation fails.
     * The response includes detailed validation errors for each field that failed.
     * 
     * @param ex the MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity containing validation error details with 400 status
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
     * Handles all other unhandled exceptions.
     * 
     * <p>Returns a 500 INTERNAL SERVER ERROR response for any unexpected exceptions
     * that were not specifically handled by other exception handlers.
     * 
     * <p>This is a fallback handler that ensures all exceptions are properly
     * converted to HTTP responses. The actual exception details are logged
     * but not exposed to the client for security reasons.
     * 
     * @param ex the unexpected exception that was thrown
     * @return ResponseEntity containing generic error details with 500 status
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
     * Error response DTO containing error information for API responses.
     * 
     * <p>This record represents the standard error response structure returned
     * to clients when exceptions occur.
     * 
     * @param timestamp the timestamp when the error occurred
     * @param status the HTTP status code
     * @param error the error type/category
     * @param message the detailed error message
     */
    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message
    ) {}
}
