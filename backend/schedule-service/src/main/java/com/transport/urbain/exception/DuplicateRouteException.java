package com.transport.urbain.exception;

/**
 * Exception thrown when attempting to create a route with a duplicate identifier.
 * <p>
 * This exception is thrown when trying to create a route that already exists,
 * typically when a route number is already in use.
 */
public class DuplicateRouteException extends RuntimeException {
    /**
     * Creates a new DuplicateRouteException with the specified message.
     *
     * @param message the detailed error message
     */
    public DuplicateRouteException(String message) {
        super(message);
    }
}
