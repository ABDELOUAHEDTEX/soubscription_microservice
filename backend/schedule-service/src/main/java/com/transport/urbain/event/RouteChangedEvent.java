package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing a change to a bus route.
 * <p>
 * Published when a route is created, updated, or its stops are modified.
 * This event is used to notify other microservices about route changes
 * that may affect schedules, maps, or passenger-facing applications.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteChangedEvent {
    /**
     * Unique identifier of the changed route
     */
    private Long routeId;

    /**
     * Route number/identifier (e.g., "Route 101")
     */
    private String routeNumber;

    /**
     * Descriptive name of the route
     */
    private String routeName;

    /**
     * Timestamp when the change occurred
     */
    private LocalDateTime timestamp;
}
