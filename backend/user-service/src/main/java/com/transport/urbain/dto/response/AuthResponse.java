package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the authentication response.
 * 
 * <p>Contains JWT tokens and user information returned after successful
 * authentication operations (login or registration).
 * 
 * <p>Includes:
 * <ul>
 *   <li>Access token for authenticating subsequent API requests</li>
 *   <li>Refresh token for obtaining new access tokens</li>
 *   <li>Token type (typically "Bearer")</li>
 *   <li>Token expiration time in seconds</li>
 *   <li>User information</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
}
