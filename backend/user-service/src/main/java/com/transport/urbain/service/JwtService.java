package com.transport.urbain.service;

import com.transport.urbain.model.User;
import io.jsonwebtoken.Claims;

/**
 * Service interface for JWT token operations.
 * 
 * <p>Provides methods for generating, validating, and extracting information from
 * JWT tokens. Supports both access tokens and refresh tokens.
 * 
 * <p>Key operations:
 * <ul>
 *   <li>Generate access tokens with user information and roles</li>
 *   <li>Generate refresh tokens for token renewal</li>
 *   <li>Extract user information from tokens</li>
 *   <li>Validate token expiration and integrity</li>
 * </ul>
 * 
 * <p>Tokens are signed using HMAC-SHA256 algorithm and include user claims
 * such as user ID, email, and roles for authorization purposes.
 */
public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    Claims extractAllClaims(String token);

    boolean isTokenValid(String token, String username);

    boolean isTokenExpired(String token);

    Long getExpirationTime();
}