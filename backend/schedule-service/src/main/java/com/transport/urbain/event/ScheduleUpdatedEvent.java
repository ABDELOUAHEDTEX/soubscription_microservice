package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing an update to a bus schedule.
 * <p>
 * Published when a schedule is modified (e.g., timing changes, bus reassignment,
 * or status updates). This event notifies other systems about schedule changes
 * that may affect real-time tracking, notifications, or passenger apps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdatedEvent {
    /**
     * Unique identifier of the updated schedule
     */
    private Long scheduleId;

    /**
     * ID of the route associated with this schedule
     */
    private Long routeId;

    /**
     * Route number for quick identification
     */
    private String routeNumber;

    /**
     * Timestamp when the update occurred
     */
    private LocalDateTime timestamp;
}
