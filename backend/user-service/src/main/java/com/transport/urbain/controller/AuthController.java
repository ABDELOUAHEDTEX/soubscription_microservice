package com.transport.urbain.controller;

import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RefreshTokenRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.dto.response.JwtResponse;
import com.transport.urbain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * 
 * <p>This controller handles HTTP requests related to user authentication,
 * including user registration, login, token refresh, and logout. These endpoints
 * are publicly accessible and do not require authentication.
 * 
 * <p>Authentication operations include:
 * <ul>
 *   <li>User registration with email and password</li>
 *   <li>User login with credentials</li>
 *   <li>Token refresh using refresh token</li>
 *   <li>User logout and token invalidation</li>
 * </ul>
 * 
 * <p>All authentication operations return JWT tokens (access token and refresh token)
 * for securing subsequent API requests. The access token is used for authentication,
 * while the refresh token is used to obtain new access tokens when they expire.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user in the system.
     * 
     * <p>Creates a new user account with the provided registration information
     * and returns authentication tokens (access token and refresh token) for
     * immediate use. The new user will be assigned a default role.
     * 
     * @param request the registration request containing user details
     * @return an authentication response containing JWT tokens and user information
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     * @throws RuntimeException if the email already exists
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Authenticates a user and returns access tokens.
     * 
     * <p>Validates the user's credentials and returns authentication tokens
     * (access token and refresh token) if the credentials are valid.
     * 
     * @param request the login request containing email and password
     * @return an authentication response containing JWT tokens and user information
     * @throws org.springframework.security.core.AuthenticationException if
     *         authentication fails (invalid credentials, locked account, etc.)
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     */
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Refreshes the access token using a valid refresh token.
     * 
     * <p>Generates a new access token when the current one expires, using the
     * refresh token. The refresh token must be valid and not revoked.
     * 
     * @param request the refresh token request containing the refresh token
     * @return a JWT response containing a new access token
     * @throws org.springframework.security.core.AuthenticationException if
     *         the refresh token is invalid or expired
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Logs out a user and invalidates their refresh token.
     * 
     * <p>Invalidates the refresh token, effectively logging out the user.
     * The access token in the Authorization header should be used to extract
     * the refresh token for invalidation.
     * 
     * @param token the authorization token containing the refresh token
     * @return no content on success
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String refreshToken = token.replace("Bearer ", "");
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
