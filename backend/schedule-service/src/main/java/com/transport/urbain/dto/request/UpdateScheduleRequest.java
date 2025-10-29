package com.transport.urbain.dto.request;

import com.transport.urbain.model.DayOfWeek;
import com.transport.urbain.model.ScheduleType;
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
 * Request DTO for updating an existing bus schedule.
 * <p>
 * All fields are optional - only provided fields will be updated.
 * This allows partial updates to schedule information without affecting
 * unspecified fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScheduleRequest {

    /**
     * New bus assignment for the schedule
     */
    private Long busId;

    /**
     * Updated departure time
     */
    private LocalTime departureTime;

    /**
     * Updated arrival time
     */
    private LocalTime arrivalTime;

    /**
     * Updated schedule type
     */
    private ScheduleType scheduleType;

    /**
     * Updated operational days
     */
    private Set<DayOfWeek> daysOfWeek;

    /**
     * Updated validity start date
     */
    private LocalDate validFrom;

    /**
     * Updated validity end date
     */
    private LocalDate validUntil;

    /**
     * Updated active status
     */
    private Boolean isActive;

    /**
     * Updated frequency in minutes
     */
    @PositiveOrZero(message = "Frequency must be zero or positive")
    private Integer frequency;

    /**
     * Updated notes
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
