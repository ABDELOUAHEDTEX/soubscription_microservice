package com.transport.urbain.service;

import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RefreshTokenRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.dto.response.JwtResponse;

/**
 * Service interface for authentication operations.
 * 
 * <p>Provides methods for user registration, login, token refresh, and logout.
 * Handles JWT token generation and refresh token management.
 * 
 * <p>Authentication flow:
 * <ul>
 *   <li>Register: Creates new user account and returns JWT tokens</li>
 *   <li>Login: Validates credentials and returns JWT tokens</li>
 *   <li>Refresh: Generates new access token using refresh token</li>
 *   <li>Logout: Revokes refresh token to invalidate session</li>
 *   <li>Revoke All: Revokes all tokens for a user (logout from all devices)</li>
 * </ul>
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    JwtResponse refreshToken(RefreshTokenRequest request);

    void logout(String token);

    void revokeAllUserTokens(Long userId);
}
