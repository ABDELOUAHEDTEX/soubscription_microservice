package com.transport.urbain.exception;

/**
 * Exception thrown when a requested route is not found in the system.
 * <p>
 * This exception is thrown when attempting to access, update, or delete a route
 * that does not exist in the database.
 */
public class RouteNotFoundException extends RuntimeException {
    /**
     * Creates a new RouteNotFoundException with the specified message.
     *
     * @param message the detailed error message
     */
    public RouteNotFoundException(String message) {
        super(message);
    }
}
