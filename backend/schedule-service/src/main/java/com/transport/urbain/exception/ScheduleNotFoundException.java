package com.transport.urbain.exception;

/**
 * Exception thrown when a requested schedule is not found in the system.
 * <p>
 * This exception is thrown when attempting to access, update, or delete a schedule
 * that does not exist in the database.
 */
public class ScheduleNotFoundException extends RuntimeException {
    /**
     * Creates a new ScheduleNotFoundException with the specified message.
     *
     * @param message the detailed error message
     */
    public ScheduleNotFoundException(String message) {
        super(message);
    }
}
