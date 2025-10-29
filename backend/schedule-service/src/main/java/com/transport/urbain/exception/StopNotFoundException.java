package com.transport.urbain.exception;

/**
 * Exception thrown when a requested bus stop is not found in the system.
 * <p>
 * This exception is thrown when attempting to access, update, or delete a stop
 * that does not exist in the database.
 */
public class StopNotFoundException extends RuntimeException {
    /**
     * Creates a new StopNotFoundException with the specified message.
     *
     * @param message the detailed error message
     */
    public StopNotFoundException(String message) {
        super(message);
    }
}
