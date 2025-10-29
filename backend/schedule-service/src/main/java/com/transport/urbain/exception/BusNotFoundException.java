package com.transport.urbain.exception;

/**
 * Exception thrown when a requested bus is not found in the system.
 * <p>
 * This exception is thrown when attempting to access, update, or delete a bus
 * that does not exist in the database.
 */
public class BusNotFoundException extends RuntimeException {
    /**
     * Creates a new BusNotFoundException with the specified message.
     *
     * @param message the detailed error message
     */
    public BusNotFoundException(String message) {
        super(message);
    }
}
