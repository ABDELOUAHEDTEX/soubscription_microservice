package com.transport.urbain.dto.response;

import com.transport.urbain.model.BusStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO representing bus information returned from API endpoints.
 * <p>
 * Contains comprehensive bus details including specifications, amenities,
 * operational status, maintenance schedule, and audit timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusResponse {

    /**
     * Unique identifier of the bus
     */
    private Long id;

    /**
     * Bus number/identifier (e.g., "BUS-001")
     */
    private String busNumber;

    /**
     * License plate number
     */
    private String licensePlate;

    /**
     * Bus model name
     */
    private String model;

    /**
     * Manufacturer name
     */
    private String manufacturer;

    /**
     * Manufacturing year
     */
    private Integer year;

    /**
     * Total passenger capacity
     */
    private Integer capacity;

    /**
     * Number of seats available
     */
    private Integer seatingCapacity;

    /**
     * Number of standing spaces available
     */
    private Integer standingCapacity;

    /**
     * Current operational status
     */
    private BusStatus status;

    /**
     * Whether the bus has WiFi capability
     */
    private Boolean hasWifi;

    /**
     * Whether the bus has air conditioning
     */
    private Boolean hasAirConditioning;

    /**
     * Whether the bus is wheelchair accessible
     */
    private Boolean isAccessible;

    /**
     * Whether the bus has GPS tracking
     */
    private Boolean hasGPS;

    /**
     * Date of last maintenance performed
     */
    private LocalDateTime lastMaintenanceDate;

    /**
     * Date of next scheduled maintenance
     */
    private LocalDateTime nextMaintenanceDate;

    /**
     * Additional notes about the bus
     */
    private String notes;

    /**
     * Timestamp when the bus was registered in the system
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the bus information was last updated
     */
    private LocalDateTime updatedAt;
}
