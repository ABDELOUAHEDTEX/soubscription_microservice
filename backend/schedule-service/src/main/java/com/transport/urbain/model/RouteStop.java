package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing the association between a route and a bus stop.
 * <p>
 * This join table defines the sequence of stops along a route, including
 * timing information and distances. Ensures each stop appears only once
 * per route at a specific sequence position.
 */
@Entity
@Table(name = "route_stops", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"route_id", "stop_id"}),
        @UniqueConstraint(columnNames = {"route_id", "sequence_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {

    /**
     * Unique identifier for the route-stop association
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Route containing this stop
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    /**
     * Stop included in this route
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    /**
     * Position of this stop in the route sequence
     */
    @Column(nullable = false)
    private Integer sequenceNumber;

    /**
     * Distance from origin stop in kilometers
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal distanceFromOrigin;

    /**
     * Estimated travel time from origin to this stop in minutes
     */
    @Column(nullable = false)
    private Integer timeFromOrigin;

    /**
     * Duration the bus stops at this location in minutes
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer dwellTime = 1;
}