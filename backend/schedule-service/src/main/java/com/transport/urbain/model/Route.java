package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a bus route.
 * <p>
 * Defines a transportation route with origin, destination, distance, duration,
 * and associated stops. Routes can be linear or circular, with color coding
 * for visual representation on maps.
 */
@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Route {

    /**
     * Unique identifier for the route
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique route number/identifier (e.g., "Route 101")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String routeNumber;

    /**
     * Descriptive name of the route
     */
    @Column(nullable = false, length = 200)
    private String routeName;

    /**
     * Detailed description of the route
     */
    @Column(length = 1000)
    private String description;

    /**
     * Starting point of the route
     */
    @Column(nullable = false, length = 200)
    private String origin;

    /**
     * End point of the route
     */
    @Column(nullable = false, length = 200)
    private String destination;

    /**
     * Total distance in kilometers
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal distance;

    /**
     * Estimated travel time in minutes
     */
    @Column(nullable = false)
    private Integer estimatedDuration;

    /**
     * Whether the route is currently active
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Whether the route returns to its origin (circular route)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isCircular = false;

    /**
     * Color code for visual representation (hex color code or color name)
     */
    @Column(length = 50)
    private String color;

    /**
     * List of stops on this route, ordered by sequence number
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();

    /**
     * List of schedules for this route
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    /**
     * Timestamp when the route was created
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the route was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Adds a stop to this route at the specified position.
     * Maintains bidirectional relationship between route and route-stop.
     *
     * @param routeStop the route-stop association to add
     */
    public void addRouteStop(RouteStop routeStop) {
        routeStops.add(routeStop);
        routeStop.setRoute(this);
    }

    /**
     * Removes a stop from this route.
     * Clears the bidirectional relationship.
     *
     * @param routeStop the route-stop association to remove
     */
    public void removeRouteStop(RouteStop routeStop) {
        routeStops.remove(routeStop);
        routeStop.setRoute(null);
    }

    /**
     * Adds a schedule to this route.
     * Maintains bidirectional relationship between route and schedule.
     *
     * @param schedule the schedule to add to this route
     */
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setRoute(this);
    }

    /**
     * Removes a schedule from this route.
     * Clears the bidirectional relationship.
     *
     * @param schedule the schedule to remove from this route
     */
    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
        schedule.setRoute(null);
    }
}
