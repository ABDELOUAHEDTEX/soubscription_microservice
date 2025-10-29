package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO representing bus stop information returned from API endpoints.
 * <p>
 * Contains comprehensive stop details including location coordinates, physical
 * features, accessibility information, and operational status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopResponse {

    /**
     * Unique identifier of the stop
     */
    private Long id;

    /**
     * Stop code/identifier (e.g., "ST-123")
     */
    private String stopCode;

    /**
     * Name of the bus stop
     */
    private String stopName;

    /**
     * Detailed description of the stop location
     */
    private String description;

    /**
     * Physical street address
     */
    private String address;

    /**
     * GPS latitude coordinate
     */
    private BigDecimal latitude;

    /**
     * GPS longitude coordinate
     */
    private BigDecimal longitude;

    /**
     * City where the stop is located
     */
    private String city;

    /**
     * District or area within the city
     */
    private String district;

    /**
     * Postal code of the stop location
     */
    private String postalCode;

    /**
     * Whether the stop has a waiting shelter
     */
    private Boolean hasWaitingShelter;

    /**
     * Whether the stop has seating available
     */
    private Boolean hasSeating;

    /**
     * Whether the stop is wheelchair accessible
     */
    private Boolean isAccessible;

    /**
     * Whether the stop is currently active
     */
    private Boolean isActive;

    /**
     * Timestamp when the stop was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the stop information was last updated
     */
    private LocalDateTime updatedAt;
}
