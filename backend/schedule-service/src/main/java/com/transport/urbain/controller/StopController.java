package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateStopRequest;
import com.transport.urbain.dto.response.StopResponse;
import com.transport.urbain.service.StopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for managing bus stops in the Urban Transport System.
 * <p>
 * This controller provides endpoints for CRUD operations on bus stops including
 * creating stops, retrieving stop information, searching for stops by location,
 * and managing stop activation status.
 * <p>
 * Bus stops represent physical locations where buses pick up and drop off passengers.
 * They include geographic coordinates, names, codes, and accessibility information.
 * <p>
 * Access control:
 * <ul>
 *     <li>GET operations are accessible to all authenticated users</li>
 *     <li>POST/PUT operations require ADMIN or OPERATOR roles</li>
 *     <li>DELETE operations require ADMIN role</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/stops")
@RequiredArgsConstructor
@Tag(name = "Stop Management", description = "Stop management endpoints")
public class StopController {

    private final StopService stopService;

    /**
     * Creates a new bus stop.
     * <p>
     * This endpoint allows administrators and operators to register a new bus stop
     * with location coordinates, name, code, and accessibility information.
     *
     * @param request the stop creation request containing location and stop details
     * @return ResponseEntity with created stop response and HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<StopResponse> createStop(@Valid @RequestBody CreateStopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stopService.createStop(request));
    }

    /**
     * Retrieves a stop by its unique identifier.
     * <p>
     * Returns detailed information about a specific bus stop including
     * location coordinates, name, code, and accessibility details.
     *
     * @param id the unique identifier of the stop
     * @return ResponseEntity containing the stop details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get stop by ID")
    public ResponseEntity<StopResponse> getStopById(@PathVariable Long id) {
        return ResponseEntity.ok(stopService.getStopById(id));
    }

    /**
     * Retrieves a stop by its unique stop code.
     * <p>
     * Stop codes are short identifiers displayed at physical stop locations
     * that allow passengers to quickly identify a specific stop.
     *
     * @param stopCode the unique stop code (e.g., "ST-123")
     * @return ResponseEntity containing the stop details
     */
    @GetMapping("/code/{stopCode}")
    @Operation(summary = "Get stop by code")
    public ResponseEntity<StopResponse> getStopByCode(@PathVariable String stopCode) {
        return ResponseEntity.ok(stopService.getStopByCode(stopCode));
    }

    /**
     * Retrieves all stops in the system with pagination support.
     * <p>
     * Returns a paginated list of all bus stops registered in the system.
     * Supports sorting and pagination through the Pageable parameter.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of stop responses
     */
    @GetMapping
    @Operation(summary = "Get all stops")
    public ResponseEntity<Page<StopResponse>> getAllStops(Pageable pageable) {
        return ResponseEntity.ok(stopService.getAllStops(pageable));
    }

    /**
     * Retrieves all currently active stops.
     * <p>
     * Returns only stops that are currently active and available for use.
     * Inactive stops are typically under maintenance or temporarily closed.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of active stops
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active stops")
    public ResponseEntity<Page<StopResponse>> getActiveStops(Pageable pageable) {
        return ResponseEntity.ok(stopService.getActiveStops(pageable));
    }

    /**
     * Searches for stops by keyword.
     * <p>
     * Searches stop names, codes, and descriptions for the given keyword.
     * Useful for finding stops by name or location description.
     *
     * @param keyword the search keyword
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of matching stops
     */
    @GetMapping("/search")
    @Operation(summary = "Search stops")
    public ResponseEntity<Page<StopResponse>> searchStops(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(stopService.searchStops(keyword, pageable));
    }

    /**
     * Retrieves nearby stops within a specified radius.
     * <p>
     * Returns all stops within the specified radius (in kilometers) from
     * the given geographic coordinates. Used for location-based queries
     * such as finding the closest stops to a user's location.
     *
     * @param latitude the latitude coordinate of the center point
     * @param longitude the longitude coordinate of the center point
     * @param radius the search radius in kilometers (default: 1.0 km)
     * @return ResponseEntity containing a list of nearby stops
     */
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby stops")
    public ResponseEntity<List<StopResponse>> getNearbyStops(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "1.0") Double radius) {
        return ResponseEntity.ok(stopService.getNearbyStops(latitude, longitude, radius));
    }

    /**
     * Updates an existing stop's information.
     * <p>
     * Allows modification of stop details such as location, name, code,
     * and accessibility information.
     *
     * @param id the unique identifier of the stop to update
     * @param request the update request containing modified stop details
     * @return ResponseEntity containing the updated stop response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<StopResponse> updateStop(
            @PathVariable Long id,
            @Valid @RequestBody CreateStopRequest request) {
        return ResponseEntity.ok(stopService.updateStop(id, request));
    }

    /**
     * Deletes a stop from the system.
     * <p>
     * This operation permanently removes a stop from the system.
     * Only administrators can perform this operation.
     *
     * @param id the unique identifier of the stop to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stop")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStop(@PathVariable Long id) {
        stopService.deleteStop(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a stop.
     * <p>
     * Marks a stop as active, making it available for use in routes and schedules.
     * Active stops appear in location and route queries.
     *
     * @param id the unique identifier of the stop to activate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> activateStop(@PathVariable Long id) {
        stopService.activateStop(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a stop.
     * <p>
     * Marks a stop as inactive, temporarily removing it from use.
     * Useful for maintenance, temporary closures, or seasonal closures.
     *
     * @param id the unique identifier of the stop to deactivate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> deactivateStop(@PathVariable Long id) {
        stopService.deactivateStop(id);
        return ResponseEntity.noContent().build();
    }
}
