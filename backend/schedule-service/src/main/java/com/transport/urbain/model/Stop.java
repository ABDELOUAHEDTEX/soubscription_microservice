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
 * Entity representing a bus stop location.
 * <p>
 * Contains geographic coordinates, location details, physical features,
 * and accessibility information. Supports location-based queries and
 * passenger-facing applications.
 */
@Entity
@Table(name = "stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Stop {

    /**
     * Unique identifier for the stop
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique stop code/identifier (e.g., "ST-123")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String stopCode;

    /**
     * Name of the bus stop
     */
    @Column(nullable = false, length = 200)
    private String stopName;

    /**
     * Detailed description of the stop location
     */
    @Column(length = 1000)
    private String description;

    /**
     * Physical street address
     */
    @Column(length = 500)
    private String address;

    /**
     * GPS latitude coordinate
     */
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * GPS longitude coordinate
     */
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * City where the stop is located
     */
    @Column(length = 100)
    private String city;

    /**
     * District or area within the city
     */
    @Column(length = 100)
    private String district;

    /**
     * Postal code of the stop location
     */
    @Column(length = 20)
    private String postalCode;

    /**
     * Whether the stop has a waiting shelter
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasWaitingShelter = false;

    /**
     * Whether the stop has seating available
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasSeating = false;

    /**
     * Whether the stop is wheelchair accessible
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccessible = false;

    /**
     * Whether the stop is currently active
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * List of routes that include this stop
     */
    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();

    /**
     * Timestamp when the stop was created
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the stop information was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
