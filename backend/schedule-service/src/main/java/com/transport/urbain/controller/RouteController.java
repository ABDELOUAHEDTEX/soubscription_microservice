package com.transport.urbain.controller;

import com.transport.urbain.dto.request.AddRouteStopRequest;
import com.transport.urbain.dto.request.CreateRouteRequest;
import com.transport.urbain.dto.response.RouteDetailsResponse;
import com.transport.urbain.dto.response.RouteResponse;
import com.transport.urbain.service.RouteService;
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

/**
 * REST controller for managing bus routes in the Urban Transport System.
 * <p>
 * This controller provides endpoints for CRUD operations on bus routes including
 * creating routes, retrieving route information, managing route stops, and
 * controlling route activation status.
 * <p>
 * Routes represent predefined paths buses follow through the city, including
 * origin, destination, and sequence of stops along the way.
 * <p>
 * Access control:
 * <ul>
 *     <li>GET operations are accessible to all authenticated users</li>
 *     <li>POST/PUT operations require ADMIN or OPERATOR roles</li>
 *     <li>DELETE operations require ADMIN role</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Route Management", description = "Route management endpoints")
public class RouteController {

    private final RouteService routeService;

    /**
     * Creates a new bus route.
     * <p>
     * This endpoint allows administrators and operators to create a new route
     * with origin, destination, and basic route information.
     * Stops can be added to the route separately.
     *
     * @param request the route creation request containing route details
     * @return ResponseEntity with created route response and HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(request));
    }

    /**
     * Retrieves a route by its unique identifier.
     * <p>
     * Returns basic route information including route number, origin,
     * destination, and active status.
     *
     * @param id the unique identifier of the route
     * @return ResponseEntity containing the route details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    /**
     * Retrieves a route by its unique route number.
     * <p>
     * Route numbers are public-facing identifiers (e.g., "Route 101")
     * displayed on buses and route information boards.
     *
     * @param routeNumber the unique route number
     * @return ResponseEntity containing the route details
     */
    @GetMapping("/number/{routeNumber}")
    @Operation(summary = "Get route by route number")
    public ResponseEntity<RouteResponse> getRouteByNumber(@PathVariable String routeNumber) {
        return ResponseEntity.ok(routeService.getRouteByNumber(routeNumber));
    }

    /**
     * Retrieves detailed route information including all stops.
     * <p>
     * Returns comprehensive route details including the complete list of stops
     * in order, along with their sequence numbers and locations.
     * Useful for displaying full route information to passengers.
     *
     * @param id the unique identifier of the route
     * @return ResponseEntity containing detailed route information with all stops
     */
    @GetMapping("/{id}/details")
    @Operation(summary = "Get route details with all stops")
    public ResponseEntity<RouteDetailsResponse> getRouteDetails(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteDetails(id));
    }

    /**
     * Retrieves all routes in the system with pagination support.
     * <p>
     * Returns a paginated list of all routes registered in the system.
     * Supports sorting and pagination through the Pageable parameter.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of route responses
     */
    @GetMapping
    @Operation(summary = "Get all routes")
    public ResponseEntity<Page<RouteResponse>> getAllRoutes(Pageable pageable) {
        return ResponseEntity.ok(routeService.getAllRoutes(pageable));
    }

    /**
     * Retrieves all currently active routes.
     * <p>
     * Returns only routes that are currently active and operational.
     * Inactive routes may be under renovation or temporarily suspended.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of active routes
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active routes")
    public ResponseEntity<Page<RouteResponse>> getActiveRoutes(Pageable pageable) {
        return ResponseEntity.ok(routeService.getActiveRoutes(pageable));
    }

    /**
     * Searches for routes by keyword.
     * <p>
     * Searches route numbers, names, origins, and destinations for the given keyword.
     * Useful for finding routes by location or route number.
     *
     * @param keyword the search keyword
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of matching routes
     */
    @GetMapping("/search")
    @Operation(summary = "Search routes")
    public ResponseEntity<Page<RouteResponse>> searchRoutes(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(routeService.searchRoutes(keyword, pageable));
    }

    /**
     * Updates an existing route's information.
     * <p>
     * Allows modification of route details such as origin, destination,
     * route number, and other attributes.
     *
     * @param id the unique identifier of the route to update
     * @param request the update request containing modified route details
     * @return ResponseEntity containing the updated route response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(id, request));
    }

    /**
     * Deletes a route from the system.
     * <p>
     * This operation permanently removes a route from the system.
     * Only administrators can perform this operation.
     *
     * @param id the unique identifier of the route to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a stop to a route.
     * <p>
     * Adds a bus stop to an existing route at the specified sequence position.
     * The sequence number determines the order of stops along the route.
     *
     * @param routeId the unique identifier of the route
     * @param request the request containing stop ID and sequence number
     * @return ResponseEntity with HTTP 201 status
     */
    @PostMapping("/{routeId}/stops")
    @Operation(summary = "Add stop to route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> addStopToRoute(
            @PathVariable Long routeId,
            @Valid @RequestBody AddRouteStopRequest request) {
        routeService.addStopToRoute(routeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Removes a stop from a route.
     * <p>
     * Removes a bus stop from a route, effectively rerouting buses
     * to skip that stop. Useful for temporary road closures or route adjustments.
     *
     * @param routeId the unique identifier of the route
     * @param stopId the unique identifier of the stop to remove
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{routeId}/stops/{stopId}")
    @Operation(summary = "Remove stop from route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> removeStopFromRoute(
            @PathVariable Long routeId,
            @PathVariable Long stopId) {
        routeService.removeStopFromRoute(routeId, stopId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a route.
     * <p>
     * Marks a route as active, making it operational for buses to use.
     * Active routes appear in passenger-facing queries and can be assigned schedules.
     *
     * @param id the unique identifier of the route to activate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> activateRoute(@PathVariable Long id) {
        routeService.activateRoute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a route.
     * <p>
     * Marks a route as inactive, temporarily suspending service on that route.
     * Useful for maintenance, seasonal closures, or emergency situations.
     *
     * @param id the unique identifier of the route to deactivate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate route")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> deactivateRoute(@PathVariable Long id) {
        routeService.deactivateRoute(id);
        return ResponseEntity.noContent().build();
    }
}
