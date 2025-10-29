package com.transport.urbain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new bus route.
 * <p>
 * Contains all information required to create a new bus route including
 * origin, destination, distance, and optional attributes like color coding.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRouteRequest {

    /**
     * Unique route number/identifier (e.g., "Route 101")
     */
    @NotBlank(message = "Route number is required")
    @Size(max = 50, message = "Route number must not exceed 50 characters")
    private String routeNumber;

    /**
     * Descriptive name of the route
     */
    @NotBlank(message = "Route name is required")
    @Size(max = 200, message = "Route name must not exceed 200 characters")
    private String routeName;

    /**
     * Detailed description of the route
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Starting point of the route
     */
    @NotBlank(message = "Origin is required")
    @Size(max = 200, message = "Origin must not exceed 200 characters")
    private String origin;

    /**
     * End point of the route
     */
    @NotBlank(message = "Destination is required")
    @Size(max = 200, message = "Destination must not exceed 200 characters")
    private String destination;

    /**
     * Total distance of the route in kilometers
     */
    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private BigDecimal distance;

    /**
     * Estimated travel time in minutes
     */
    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Estimated duration must be positive")
    private Integer estimatedDuration;

    /**
     * Whether the route returns to its origin (circular route)
     */
    private Boolean isCircular;

    /**
     * Color code for visual representation (hex code or color name)
     */
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;
}
