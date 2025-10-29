package com.transport.urbain.dto.request;

import com.transport.urbain.model.DayOfWeek;
import com.transport.urbain.model.ScheduleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Request DTO for creating a new bus schedule.
 * <p>
 * Contains all information required to create a schedule entry including
 * route, bus assignment, timing, and operational days.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleRequest {

    /**
     * ID of the route for which the schedule is created
     */
    @NotNull(message = "Route ID is required")
    private Long routeId;

    /**
     * ID of the bus assigned to this schedule (optional)
     */
    private Long busId;

    /**
     * Scheduled departure time from the origin stop
     */
    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;

    /**
     * Scheduled arrival time at the destination stop
     */
    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;

    /**
     * Type of schedule (e.g., REGULAR, SPECIAL, HOLIDAY)
     */
    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    /**
     * Set of days when this schedule operates (e.g., weekdays)
     */
    @NotNull(message = "Days of week are required")
    @Size(min = 1, message = "At least one day must be selected")
    private Set<DayOfWeek> daysOfWeek;

    /**
     * Date from which this schedule becomes valid
     */
    private LocalDate validFrom;

    /**
     * Date until which this schedule is valid
     */
    private LocalDate validUntil;

    /**
     * Frequency of operation in minutes (e.g., 15 for every 15 minutes)
     */
    @PositiveOrZero(message = "Frequency must be zero or positive")
    private Integer frequency;

    /**
     * Additional notes about the schedule
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
