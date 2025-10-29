package com.transport.urbain.exception;

/**
 * Exception thrown when a JWT token has expired or is invalid.
 * 
 * <p>This exception is thrown during token validation when:
 * <ul>
 *   <li>The access token has expired</li>
 *   <li>The token signature is invalid</li>
 *   <li>The token has been revoked</li>
 *   <li>The token is malformed or corrupt</li>
 * </ul>
 * 
 * <p>When thrown, this exception results in a 401 UNAUTHORIZED HTTP response.
 * The client should use the refresh token to obtain a new access token, or
 * re-authenticate if the refresh token is also invalid.
 */
public class TokenExpiredException extends RuntimeException {
    /**
     * Constructs a new TokenExpiredException with the specified message.
     * 
     * @param message the detail message explaining why the token is invalid
     */
    public TokenExpiredException(String message) {
        super(message);
    }
}
