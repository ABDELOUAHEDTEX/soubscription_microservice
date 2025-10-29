package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO representing route information returned from API endpoints.
 * <p>
 * Contains route details including origin, destination, distance, duration,
 * and metadata about the route's operational characteristics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {

    /**
     * Unique identifier of the route
     */
    private Long id;

    /**
     * Route number/identifier (e.g., "Route 101")
     */
    private String routeNumber;

    /**
     * Descriptive name of the route
     */
    private String routeName;

    /**
     * Detailed description of the route
     */
    private String description;

    /**
     * Starting point of the route
     */
    private String origin;

    /**
     * End point of the route
     */
    private String destination;

    /**
     * Total distance in kilometers
     */
    private BigDecimal distance;

    /**
     * Estimated travel time in minutes
     */
    private Integer estimatedDuration;

    /**
     * Whether the route is currently active
     */
    private Boolean isActive;

    /**
     * Whether the route returns to its origin (circular route)
     */
    private Boolean isCircular;

    /**
     * Color code for visual representation
     */
    private String color;

    /**
     * Total number of stops on this route
     */
    private Integer numberOfStops;

    /**
     * Timestamp when the route was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the route was last updated
     */
    private LocalDateTime updatedAt;
}
