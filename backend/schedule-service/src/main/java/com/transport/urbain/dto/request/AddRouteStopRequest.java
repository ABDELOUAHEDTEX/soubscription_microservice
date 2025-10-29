package com.transport.urbain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for adding a stop to an existing route.
 * <p>
 * Contains information about how a stop should be integrated into a route,
 * including its position in the sequence and timing information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddRouteStopRequest {

    /**
     * ID of the stop to add to the route
     */
    @NotNull(message = "Stop ID is required")
    private Long stopId;

    /**
     * Position of this stop in the route sequence (0-based or 1-based)
     */
    @NotNull(message = "Sequence number is required")
    @PositiveOrZero(message = "Sequence number must be zero or positive")
    private Integer sequenceNumber;

    /**
     * Distance from the origin stop in kilometers
     */
    @NotNull(message = "Distance from origin is required")
    @PositiveOrZero(message = "Distance must be zero or positive")
    private BigDecimal distanceFromOrigin;

    /**
     * Estimated travel time from origin to this stop in minutes
     */
    @NotNull(message = "Time from origin is required")
    @PositiveOrZero(message = "Time must be zero or positive")
    private Integer timeFromOrigin;

    /**
     * Dwell time (time the bus waits at this stop) in seconds
     */
    @Positive(message = "Dwell time must be positive")
    private Integer dwellTime;
}