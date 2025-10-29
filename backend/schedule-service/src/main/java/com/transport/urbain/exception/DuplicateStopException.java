package com.transport.urbain.exception;

/**
 * Exception thrown when attempting to create a stop with a duplicate identifier.
 * <p>
 * This exception is thrown when trying to register a bus stop that already exists,
 * typically when a stop code is already in use.
 */
public class DuplicateStopException extends RuntimeException {
    /**
     * Creates a new DuplicateStopException with the specified message.
     *
     * @param message the detailed error message
     */
    public DuplicateStopException(String message) {
        super(message);
    }
}