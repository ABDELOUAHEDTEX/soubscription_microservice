package com.transport.urbain.controller;

import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RefreshTokenRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.dto.response.JwtResponse;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthController.
 * 
 * <p>Tests authentication endpoints including:
 * <ul>
 *   <li>User registration</li>
 *   <li>User login</li>
 *   <li>Token refresh</li>
 *   <li>User logout</li>
 * </ul>
 * 
 * <p>Verifies HTTP responses, service method calls, and proper handling
 * of authentication operations.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .firstName("John")
                .lastName("Doe")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(new UserResponse())
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.register(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("accessToken");

        verify(authService).register(request);
    }

    @Test
    void shouldLoginUserSuccessfully() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(new UserResponse())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("accessToken");

        verify(authService).login(request);
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("newAccessToken")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(jwtResponse);

        // When
        ResponseEntity<JwtResponse> response = authController.refreshToken(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("newAccessToken");

        verify(authService).refreshToken(request);
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        // Given
        String bearerToken = "Bearer refreshToken";
        doNothing().when(authService).logout(anyString());

        // When
        ResponseEntity<Void> response = authController.logout(bearerToken);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(authService).logout("refreshToken");
    }

    @Test
    void shouldHandleLogoutWithoutBearerPrefix() {
        // Given
        String tokenWithoutPrefix = "refreshToken";
        doNothing().when(authService).logout(anyString());

        // When
        ResponseEntity<Void> response = authController.logout(tokenWithoutPrefix);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(authService).logout(tokenWithoutPrefix);
    }
}

