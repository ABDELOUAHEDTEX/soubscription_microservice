package com.transport.urbain.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 * 
 * <p>This exception is thrown during login attempts when the provided
 * email/password combination is incorrect, the account is locked, or
 * the account is disabled.
 * 
 * <p>When thrown, this exception results in a 401 UNAUTHORIZED HTTP response.
 * For security reasons, the error message should not reveal whether the email
 * exists or whether the password was wrong.
 * 
 * <p>Common scenarios:
 * <ul>
 *   <li>Incorrect email or password</li>
 *   <li>Account is locked due to multiple failed attempts</li>
 *   <li>Account is disabled</li>
 *   <li>Account has expired</li>
 * </ul>
 */
public class InvalidCredentialsException extends RuntimeException {
    /**
     * Constructs a new InvalidCredentialsException with the specified message.
     * 
     * @param message the detail message explaining why authentication failed
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
