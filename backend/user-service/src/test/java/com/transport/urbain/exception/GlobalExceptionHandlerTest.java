package com.transport.urbain.exception;

import com.transport.urbain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GlobalExceptionHandler.
 * 
 * <p>Tests exception handling and error response formatting including:
 * <ul>
 *   <li>UserNotFoundException handling</li>
 *   <li>DuplicateUserException handling</li>
 *   <li>InvalidCredentialsException handling</li>
 *   <li>TokenExpiredException handling</li>
 *   <li>AccessDeniedException handling</li>
 *   <li>Validation exception handling</li>
 *   <li>Global exception fallback</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Correct HTTP status codes</li>
 *   <li>Proper error message formatting</li>
 *   <li>Timestamp inclusion in error responses</li>
 *   <li>JSON error structure</li>
 * </ul>
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleUserNotFoundException() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("User not found");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleUserNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("User Not Found");
        assertThat(response.getBody().message()).isEqualTo("User not found");
    }

    @Test
    void shouldHandleDuplicateUserException() {
        // Given
        DuplicateUserException exception = new DuplicateUserException("Email already exists");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleDuplicateUserException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Duplicate User");
        assertThat(response.getBody().message()).isEqualTo("Email already exists");
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        // Given
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleInvalidCredentialsException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Invalid Credentials");
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void shouldHandleTokenExpiredException() {
        // Given
        TokenExpiredException exception = new TokenExpiredException("Token expired");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleTokenExpiredException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Token Expired");
        assertThat(response.getBody().message()).isEqualTo("Token expired");
    }

    @Test
    void shouldHandleAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleAccessDeniedException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(403);
        assertThat(response.getBody().error()).isEqualTo("Access Denied");
        assertThat(response.getBody().message()).isEqualTo("You don't have permission to access this resource");
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        
        FieldError fieldError = new FieldError("user", "email", "Email is required");
        List<FieldError> fieldErrors = List.of(fieldError);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.copyOf(fieldErrors));

        // When
        ResponseEntity<Map<String, Object>> response = 
                globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Validation Failed");
    }

    @Test
    void shouldHandleGlobalException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleGlobalException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }

    @Test
    void shouldHandleExceptionResponseWithCorrectTimestamp() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("Test message");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                globalExceptionHandler.handleUserNotFoundException(exception);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().timestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(response.getBody().timestamp()).isAfter(LocalDateTime.now().minusSeconds(1));
    }
}

