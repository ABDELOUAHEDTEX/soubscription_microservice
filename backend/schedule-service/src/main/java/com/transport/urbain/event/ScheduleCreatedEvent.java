package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Event representing the creation of a new bus schedule.
 * <p>
 * Published when a new schedule is added to the system. This event notifies
 * other microservices about new schedule entries that may need to be indexed,
 * displayed to passengers, or used for operational planning.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreatedEvent {
    /**
     * Unique identifier of the created schedule
     */
    private Long scheduleId;

    /**
     * ID of the route for this schedule
     */
    private Long routeId;

    /**
     * Route number for quick identification
     */
    private String routeNumber;

    /**
     * Scheduled departure time from origin stop
     */
    private LocalTime departureTime;

    /**
     * Timestamp when the schedule was created
     */
    private LocalDateTime timestamp;
}
