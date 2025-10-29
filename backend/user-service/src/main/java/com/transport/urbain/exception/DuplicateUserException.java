package com.transport.urbain.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 * 
 * <p>This exception is thrown during user registration when the provided email
 * or username already exists in the system. It indicates a violation of the
 * unique constraint on user identifiers.
 * 
 * <p>When thrown, this exception results in a 409 CONFLICT HTTP response
 * indicating that the requested resource already exists.
 */
public class DuplicateUserException extends RuntimeException {
    /**
     * Constructs a new DuplicateUserException with the specified message.
     * 
     * @param message the detail message explaining which user already exists
     */
    public DuplicateUserException(String message) {
        super(message);
    }
}
