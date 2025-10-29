package com.transport.urbain.exception;

/**
 * Exception thrown when attempting to create a bus with a duplicate identifier.
 * <p>
 * This exception is thrown when trying to register a bus that already exists,
 * typically when a bus number or license plate is already in use.
 */
public class DuplicateBusException extends RuntimeException {
    /**
     * Creates a new DuplicateBusException with the specified message.
     *
     * @param message the detailed error message
     */
    public DuplicateBusException(String message) {
        super(message);
    }
}
