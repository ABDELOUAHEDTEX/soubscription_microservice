package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateScheduleRequest;
import com.transport.urbain.dto.request.SearchScheduleRequest;
import com.transport.urbain.dto.request.UpdateScheduleRequest;
import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * REST controller for managing bus schedules in the Urban Transport System.
 * <p>
 * This controller provides endpoints for CRUD operations on bus schedules including
 * creating schedules, retrieving schedule information, searching schedules, and
 * managing schedule activation status.
 * <p>
 * Schedules represent the timetable for buses operating on specific routes,
 * including departure times, stops, and associated buses.
 * <p>
 * Access control:
 * <ul>
 *     <li>GET operations are accessible to all authenticated users</li>
 *     <li>POST/PUT operations require ADMIN or OPERATOR roles</li>
 *     <li>DELETE operations require ADMIN role</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule Management", description = "Schedule management endpoints")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Creates a new bus schedule.
     * <p>
     * This endpoint allows administrators and operators to create schedules
     * for buses operating on specific routes with specified departure times.
     *
     * @param request the schedule creation request containing route, bus, and timing details
     * @return ResponseEntity with created schedule response and HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(request));
    }

    /**
     * Retrieves a schedule by its unique identifier.
     * <p>
     * Returns detailed information about a specific schedule including route,
     * bus, departure time, and active status.
     *
     * @param id the unique identifier of the schedule
     * @return ResponseEntity containing the schedule details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    /**
     * Retrieves all schedules in the system with pagination support.
     * <p>
     * Returns a paginated list of all schedules registered in the system.
     * Supports sorting and pagination through the Pageable parameter.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of schedule responses
     */
    @GetMapping
    @Operation(summary = "Get all schedules")
    public ResponseEntity<Page<ScheduleResponse>> getAllSchedules(Pageable pageable) {
        return ResponseEntity.ok(scheduleService.getAllSchedules(pageable));
    }

    /**
     * Retrieves all schedules for a specific route.
     * <p>
     * Returns all schedules associated with a given route, showing when
     * buses operate on that route throughout the day.
     *
     * @param routeId the unique identifier of the route
     * @return ResponseEntity containing a list of schedules for the specified route
     */
    @GetMapping("/route/{routeId}")
    @Operation(summary = "Get schedules by route")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRoute(routeId));
    }

    /**
     * Retrieves all currently active schedules.
     * <p>
     * Returns only schedules that are currently active (available for buses
     * to operate on). Inactive schedules are typically archived or temporarily disabled.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a page of active schedules
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active schedules")
    public ResponseEntity<Page<ScheduleResponse>> getActiveSchedules(Pageable pageable) {
        return ResponseEntity.ok(scheduleService.getActiveSchedules(pageable));
    }

    /**
     * Searches for schedules based on various criteria.
     * <p>
     * Allows searching for schedules by route, bus, time range, and other
     * specified criteria in the search request.
     *
     * @param request the search request containing search criteria
     * @return ResponseEntity containing a list of matching schedules
     */
    @PostMapping("/search")
    @Operation(summary = "Search schedules")
    public ResponseEntity<List<ScheduleResponse>> searchSchedules(@RequestBody SearchScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.searchSchedules(request));
    }

    /**
     * Retrieves today's schedules for a specific route.
     * <p>
     * Returns all schedules operating today on the specified route,
     * useful for passengers checking current day's bus timings.
     *
     * @param routeId the unique identifier of the route
     * @return ResponseEntity containing a list of today's schedules for the route
     */
    @GetMapping("/route/{routeId}/today")
    @Operation(summary = "Get today's schedules for a route")
    public ResponseEntity<List<ScheduleResponse>> getTodaySchedules(@PathVariable Long routeId) {
        return ResponseEntity.ok(scheduleService.getTodaySchedules(routeId));
    }

    /**
     * Retrieves upcoming schedules for a route starting from a specific time.
     * <p>
     * Returns all future schedule entries for a route starting from the
     * specified time. Useful for finding the next available buses.
     *
     * @param routeId the unique identifier of the route
     * @param fromTime the time from which to retrieve upcoming schedules
     * @return ResponseEntity containing a list of upcoming schedules
     */
    @GetMapping("/route/{routeId}/upcoming")
    @Operation(summary = "Get upcoming schedules for a route")
    public ResponseEntity<List<ScheduleResponse>> getUpcomingSchedules(
            @PathVariable Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fromTime) {
        return ResponseEntity.ok(scheduleService.getUpcomingSchedules(routeId, fromTime));
    }

    /**
     * Updates an existing schedule's information.
     * <p>
     * Allows modification of schedule details such as departure time, bus assignment,
     * and other scheduling attributes.
     *
     * @param id the unique identifier of the schedule to update
     * @param request the update request containing modified schedule details
     * @return ResponseEntity containing the updated schedule response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    /**
     * Deletes a schedule from the system.
     * <p>
     * This operation permanently removes a schedule from the system.
     * Only administrators can perform this operation.
     *
     * @param id the unique identifier of the schedule to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a schedule.
     * <p>
     * Marks a schedule as active, making it available for buses to operate on.
     * Active schedules appear in route and availability queries.
     *
     * @param id the unique identifier of the schedule to activate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> activateSchedule(@PathVariable Long id) {
        scheduleService.activateSchedule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a schedule.
     * <p>
     * Marks a schedule as inactive, preventing buses from using it.
     * Useful for temporary schedule suspensions or maintenance periods.
     *
     * @param id the unique identifier of the schedule to deactivate
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> deactivateSchedule(@PathVariable Long id) {
        scheduleService.deactivateSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
