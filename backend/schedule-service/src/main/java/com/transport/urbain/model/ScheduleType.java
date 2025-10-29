package com.transport.urbain.model;

/**
 * Enum representing the type of bus schedule.
 * <p>
 * Defines various schedule categories used to differentiate bus operations
 * based on time periods, service patterns, or special events.
 */
public enum ScheduleType {
    /**
     * Regular weekday service schedule
     */
    REGULAR,

    /**
     * Express service with limited stops
     */
    EXPRESS,

    /**
     * Night service operating during late hours
     */
    NIGHT,

    /**
     * Weekend-only service schedule
     */
    WEEKEND,

    /**
     * Service operating on holidays
     */
    HOLIDAY,

    /**
     * Special event or temporary service schedule
     */
    SPECIAL
}
