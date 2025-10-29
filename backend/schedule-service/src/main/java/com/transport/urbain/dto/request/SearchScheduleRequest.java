package com.transport.urbain.dto.request;

import com.transport.urbain.model.DayOfWeek;
import com.transport.urbain.model.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for searching and filtering bus schedules.
 * <p>
 * Allows flexible searching of schedules based on various criteria such as
 * route, time range, date, and operational status. All fields are optional,
 * enabling broad or narrow searches as needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchScheduleRequest {

    /**
     * Filter by specific route ID
     */
    private Long routeId;

    /**
     * Filter by route number (e.g., "Route 101")
     */
    private String routeNumber;

    /**
     * Filter by origin stop ID
     */
    private Long originStopId;

    /**
     * Filter by destination stop ID
     */
    private Long destinationStopId;

    /**
     * Filter by specific date
     */
    private LocalDate date;

    /**
     * Filter by departure time from this time onwards
     */
    private LocalTime fromTime;

    /**
     * Filter by departure time up to this time
     */
    private LocalTime toTime;

    /**
     * Filter by day of the week
     */
    private DayOfWeek dayOfWeek;

    /**
     * Filter by schedule type (REGULAR, SPECIAL, etc.)
     */
    private ScheduleType scheduleType;

    /**
     * Filter by active status (true for active, false for inactive)
     */
    private Boolean isActive;
}
