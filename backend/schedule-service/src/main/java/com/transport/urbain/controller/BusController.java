package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateBusRequest;
import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.model.BusStatus;
import com.transport.urbain.service.BusService;
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

import java.util.List;

/**
 * REST controller for managing buses in the Urban Transport System.
 * <p>
 * This controller provides endpoints for CRUD operations on buses including
 * creating new buses, retrieving bus information, updating bus details,
 * and managing bus availability status.
 * <p>
 * Access control:
 * <ul>
 *     <li>GET operations are accessible to all authenticated users</li>
 *     <li>POST/PUT operations require ADMIN or OPERATOR roles</li>
 *     <li>DELETE operations require ADMIN role</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
@Tag(name = "Bus Management", description = "Bus management endpoints")
public class BusController {

    private final BusService busService;

    /**
     * Creates a new bus in the system.
     * <p>
     * This endpoint allows administrators and operators to register a new bus
     * with specifications including bus number, capacity, and status.
     *
     * @param request the bus creation request containing bus details
     * @return ResponseEntity with created bus response and HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new bus")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody CreateBusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.createBus(request));
    }

    /**
     * Retrieves a bus by its unique identifier.
     * <p>
     * Returns detailed information about a specific bus including its
     * capacity, current status, and associated route information.
     *
     * @param id the unique identifier of the bus
     * @return ResponseEntity containing the bus details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get bus by ID")
    public ResponseEntity<BusResponse> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    /**
     * Retrieves a bus by its bus number.
     * <p>
     * Bus numbers are unique identifiers assigned to each bus in the fleet.
     * This endpoint allows lookup of buses by their assigned number.
     *
     * @param busNumber the unique bus number (e.g., "BUS-001")
     * @return ResponseEntity containing the bus details
     */
    @GetMapping("/number/{busNumber}")
    @Operation(summary = "Get bus by bus number")
    public ResponseEntity<BusResponse> getBusByNumber(@PathVariable String busNumber) {
        return ResponseEntity.ok(busService.getBusByNumber(busNumber));
    }

    /**
     * Retrieves all buses in the system with pagination support.
     * <p>
     * Returns a paginated list of all buses registered in the system.
     * Supports sorting and pagination through the Pageable parameter.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of bus responses
     */
    @GetMapping
    @Operation(summary = "Get all buses")
    public ResponseEntity<Page<BusResponse>> getAllBuses(Pageable pageable) {
        return ResponseEntity.ok(busService.getAllBuses(pageable));
    }

    /**
     * Retrieves all buses that are currently available for service.
     * <p>
     * This endpoint returns only buses that are in AVAILABLE status,
     * which can be assigned to routes and schedules.
     *
     * @return ResponseEntity containing a list of available buses
     */
    @GetMapping("/available")
    @Operation(summary = "Get all available buses")
    public ResponseEntity<List<BusResponse>> getAvailableBuses() {
        return ResponseEntity.ok(busService.getAvailableBuses());
    }

    /**
     * Retrieves buses filtered by their current status.
     * <p>
     * Supports filtering buses by status such as AVAILABLE, IN_USE, MAINTENANCE, etc.
     *
     * @param status the bus status to filter by
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of buses with the specified status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get buses by status")
    public ResponseEntity<Page<BusResponse>> getBusesByStatus(
            @PathVariable BusStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(busService.getBusesByStatus(status, pageable));
    }

    /**
     * Updates an existing bus's information.
     * <p>
     * Allows modification of bus details such as capacity, number, and other attributes.
     * Only the bus ID is required; other fields in the request are updated as provided.
     *
     * @param id the unique identifier of the bus to update
     * @param request the update request containing modified bus details
     * @return ResponseEntity containing the updated bus response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update bus")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<BusResponse> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody CreateBusRequest request) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    /**
     * Deletes a bus from the system.
     * <p>
     * This operation permanently removes a bus from the system.
     * Only administrators can perform this operation.
     *
     * @param id the unique identifier of the bus to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the status of a specific bus.
     * <p>
     * Allows changing a bus's status (e.g., AVAILABLE, IN_USE, MAINTENANCE).
     * This is useful for tracking the operational state of buses in the fleet.
     *
     * @param id the unique identifier of the bus
     * @param status the new status to apply to the bus
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update bus status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> updateBusStatus(
            @PathVariable Long id,
            @RequestParam BusStatus status) {
        busService.updateBusStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
