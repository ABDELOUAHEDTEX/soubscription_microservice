package com.transport.urbain.exception;

/**
 * Exception thrown when a schedule operation is invalid.
 * <p>
 * This exception is thrown when attempting to create or update a schedule
 * with invalid parameters, such as:
 * <ul>
 *     <li>Invalid time ranges (arrival before departure)</li>
 *     <li>Overlapping schedules</li>
 *     <li>Invalid bus assignments</li>
 *     <li>Incompatible schedule types</li>
 * </ul>
 */
public class InvalidScheduleException extends RuntimeException {
    /**
     * Creates a new InvalidScheduleException with the specified message.
     *
     * @param message the detailed error message
     */
    public InvalidScheduleException(String message) {
        super(message);
    }
}
