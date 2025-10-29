package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a JWT token response.
 * 
 * <p>Contains the new access token generated during token refresh operations.
 * Used to return a fresh access token when the previous one expires.
 * 
 * <p>Includes:
 * <ul>
 *   <li>Access token for authenticating API requests</li>
 *   <li>Token type (typically "Bearer")</li>
 *   <li>Token expiration time in seconds</li>
 * </ul>
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
