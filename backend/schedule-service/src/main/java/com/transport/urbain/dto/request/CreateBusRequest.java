package com.transport.urbain.dto.request;

import com.transport.urbain.model.BusStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new bus.
 * <p>
 * Contains all information required to register a new bus in the system,
 * including vehicle specifications, amenities, and initial status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBusRequest {

    /**
     * Unique bus number/identifier (e.g., "BUS-001")
     */
    @NotBlank(message = "Bus number is required")
    @Size(max = 50, message = "Bus number must not exceed 50 characters")
    private String busNumber;

    /**
     * Vehicle license plate number
     */
    @NotBlank(message = "License plate is required")
    @Size(max = 50, message = "License plate must not exceed 50 characters")
    private String licensePlate;

    /**
     * Bus model name
     */
    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    /**
     * Bus manufacturer name
     */
    @NotBlank(message = "Manufacturer is required")
    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    private String manufacturer;

    /**
     * Manufacturing year of the bus
     */
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2100, message = "Year must be before 2100")
    private Integer year;

    /**
     * Total passenger capacity
     */
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    /**
     * Number of seated passengers (optional)
     */
    @PositiveOrZero(message = "Seating capacity must be zero or positive")
    private Integer seatingCapacity;

    /**
     * Number of standing passengers (optional)
     */
    @PositiveOrZero(message = "Standing capacity must be zero or positive")
    private Integer standingCapacity;

    /**
     * Current operational status of the bus
     */
    @NotNull(message = "Status is required")
    private BusStatus status;

    /**
     * Whether the bus has WiFi connectivity
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
     * Additional notes or comments about the bus
     */
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}

