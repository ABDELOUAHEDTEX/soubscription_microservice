package com.transport.urbain.dto.response;

import com.transport.urbain.model.DayOfWeek;
import com.transport.urbain.model.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Response DTO representing schedule information returned from API endpoints.
 * <p>
 * Contains comprehensive schedule details including timing, route information,
 * bus assignment, operational days, and validity periods. Flattens related
 * route and bus information for easier consumption by API clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {

    /**
     * Unique identifier of the schedule
     */
    private Long id;

    /**
     * ID of the associated route
     */
    private Long routeId;

    /**
     * Route number for quick identification
     */
    private String routeNumber;

    /**
     * Route name for display purposes
     */
    private String routeName;

    /**
     * ID of the assigned bus (null if no bus assigned yet)
     */
    private Long busId;

    /**
     * Bus number/identifier (null if no bus assigned)
     */
    private String busNumber;

    /**
     * Scheduled departure time from origin stop
     */
    private LocalTime departureTime;

    /**
     * Scheduled arrival time at destination stop
     */
    private LocalTime arrivalTime;

    /**
     * Type of schedule (REGULAR, SPECIAL, HOLIDAY, etc.)
     */
    private ScheduleType scheduleType;

    /**
     * Set of days when this schedule operates
     */
    private Set<DayOfWeek> daysOfWeek;

    /**
     * Date from which this schedule is valid
     */
    private LocalDate validFrom;

    /**
     * Date until which this schedule is valid
     */
    private LocalDate validUntil;

    /**
     * Whether the schedule is currently active
     */
    private Boolean isActive;

    /**
     * Operating frequency in minutes (for recurring schedules)
     */
    private Integer frequency;

    /**
     * Additional notes about the schedule
     */
    private String notes;

    /**
     * Timestamp when the schedule was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the schedule was last updated
     */
    private LocalDateTime updatedAt;
}
