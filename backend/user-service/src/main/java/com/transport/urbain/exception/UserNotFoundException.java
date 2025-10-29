package com.transport.urbain.exception;

/**
 * Exception thrown when a requested user cannot be found.
 * 
 * <p>This exception is thrown when attempting to retrieve, update, or delete
 * a user that does not exist in the system. The exception message typically
 * contains the user ID or email address that was not found.
 * 
 * <p>When thrown, this exception results in a 404 NOT FOUND HTTP response
 * with the error message returned to the client.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructs a new UserNotFoundException with the specified message.
     * 
     * @param message the detail message explaining which user was not found
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
