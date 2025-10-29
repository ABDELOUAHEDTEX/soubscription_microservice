package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO representing detailed route information including all stops.
 * <p>
 * Extends basic route information with a complete list of stops in sequence order,
 * providing comprehensive details for displaying full route information to passengers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDetailsResponse {

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
     * List of all stops on the route in sequence order
     */
    private List<RouteStopDetail> stops;

    /**
     * Detailed information about a stop within a route.
     * <p>
     * Contains location, sequence position, and timing information for a single stop.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteStopDetail {
        /**
         * Unique identifier of the stop
         */
        private Long stopId;

        /**
         * Stop code/identifier (e.g., "ST-123")
         */
        private String stopCode;

        /**
         * Name of the stop
         */
        private String stopName;

        /**
         * GPS latitude coordinate
         */
        private BigDecimal latitude;

        /**
         * GPS longitude coordinate
         */
        private BigDecimal longitude;

        /**
         * Position of this stop in the route sequence
         */
        private Integer sequenceNumber;

        /**
         * Distance from route origin in kilometers
         */
        private BigDecimal distanceFromOrigin;

        /**
         * Estimated travel time from origin to this stop in minutes
         */
        private Integer timeFromOrigin;

        /**
         * Dwell time (time bus waits at this stop) in seconds
         */
        private Integer dwellTime;
    }
}