package com.transport.urbain.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new bus stop.
 * <p>
 * Contains all information required to register a new bus stop including
 * location coordinates, physical features, and accessibility information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStopRequest {

    /**
     * Unique stop code/identifier (e.g., "ST-123")
     */
    @NotBlank(message = "Stop code is required")
    @Size(max = 50, message = "Stop code must not exceed 50 characters")
    private String stopCode;

    /**
     * Name of the bus stop
     */
    @NotBlank(message = "Stop name is required")
    @Size(max = 200, message = "Stop name must not exceed 200 characters")
    private String stopName;

    /**
     * Detailed description of the stop location
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Physical street address of the stop
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    /**
     * GPS latitude coordinate
     */
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    /**
     * GPS longitude coordinate
     */
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    /**
     * City where the stop is located
     */
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    /**
     * District or area within the city
     */
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    /**
     * Postal code of the stop location
     */
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
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
}
