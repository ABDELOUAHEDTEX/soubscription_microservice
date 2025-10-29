package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateScheduleRequest;
import com.transport.urbain.dto.request.SearchScheduleRequest;
import com.transport.urbain.dto.request.UpdateScheduleRequest;
import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduleController.
 * <p>
 * This test class covers all REST endpoints in ScheduleController including:
 * <ul>
 *     <li>Schedule creation and retrieval</li>
 *     <li>Schedule update and deletion</li>
 *     <li>Schedule search functionality</li>
 *     <li>Schedule activation/deactivation</li>
 *     <li>Time-based queries</li>
 *     <li>Response status codes and body validation</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private CreateScheduleRequest createScheduleRequest;
    private ScheduleResponse scheduleResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for CreateScheduleRequest and ScheduleResponse.
     */
    @BeforeEach
    void setUp() {
        Set<String> daysOfWeek = new HashSet<>();
        daysOfWeek.add("MONDAY");
        daysOfWeek.add("WEDNESDAY");

        createScheduleRequest = new CreateScheduleRequest();
        createScheduleRequest.setRouteId(1L);
        createScheduleRequest.setDepartureTime(LocalTime.of(8, 0));
        createScheduleRequest.setArrivalTime(LocalTime.of(9, 30));

        scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(1L);
        scheduleResponse.setRouteId(1L);
        scheduleResponse.setIsActive(true);
    }

    /**
     * Tests successful schedule creation via POST endpoint.
     * Verifies that HTTP 201 status is returned.
     */
    @Test
    void testCreateSchedule_Success() {
        // Arrange
        when(scheduleService.createSchedule(any(CreateScheduleRequest.class))).thenReturn(scheduleResponse);

        // Act
        ResponseEntity<ScheduleResponse> response = scheduleController.createSchedule(createScheduleRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(scheduleService, times(1)).createSchedule(any(CreateScheduleRequest.class));
    }

    /**
     * Tests retrieval of schedule by ID via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetScheduleById_Success() {
        // Arrange
        when(scheduleService.getScheduleById(1L)).thenReturn(scheduleResponse);

        // Act
        ResponseEntity<ScheduleResponse> response = scheduleController.getScheduleById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(scheduleService, times(1)).getScheduleById(1L);
    }

    /**
     * Tests retrieval of all schedules via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetAllSchedules_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ScheduleResponse> schedulePage = new PageImpl<>(List.of(scheduleResponse));
        when(scheduleService.getAllSchedules(pageable)).thenReturn(schedulePage);

        // Act
        ResponseEntity<Page<ScheduleResponse>> response = scheduleController.getAllSchedules(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(scheduleService, times(1)).getAllSchedules(pageable);
    }

    /**
     * Tests retrieval of schedules by route via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetSchedulesByRoute_Success() {
        // Arrange
        when(scheduleService.getSchedulesByRoute(1L)).thenReturn(List.of(scheduleResponse));

        // Act
        ResponseEntity<List<ScheduleResponse>> response = scheduleController.getSchedulesByRoute(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(scheduleService, times(1)).getSchedulesByRoute(1L);
    }

    /**
     * Tests retrieval of active schedules via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetActiveSchedules_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ScheduleResponse> schedulePage = new PageImpl<>(List.of(scheduleResponse));
        when(scheduleService.getActiveSchedules(pageable)).thenReturn(schedulePage);

        // Act
        ResponseEntity<Page<ScheduleResponse>> response = scheduleController.getActiveSchedules(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(scheduleService, times(1)).getActiveSchedules(pageable);
    }

    /**
     * Tests schedule search via POST endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testSearchSchedules_Success() {
        // Arrange
        SearchScheduleRequest request = new SearchScheduleRequest();
        request.setRouteId(1L);
        when(scheduleService.searchSchedules(request)).thenReturn(List.of(scheduleResponse));

        // Act
        ResponseEntity<List<ScheduleResponse>> response = scheduleController.searchSchedules(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(scheduleService, times(1)).searchSchedules(request);
    }

    /**
     * Tests retrieval of today's schedules via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetTodaySchedules_Success() {
        // Arrange
        when(scheduleService.getTodaySchedules(1L)).thenReturn(List.of(scheduleResponse));

        // Act
        ResponseEntity<List<ScheduleResponse>> response = scheduleController.getTodaySchedules(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(scheduleService, times(1)).getTodaySchedules(1L);
    }

    /**
     * Tests retrieval of upcoming schedules via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetUpcomingSchedules_Success() {
        // Arrange
        LocalTime fromTime = LocalTime.of(10, 0);
        when(scheduleService.getUpcomingSchedules(1L, fromTime)).thenReturn(List.of(scheduleResponse));

        // Act
        ResponseEntity<List<ScheduleResponse>> response = scheduleController.getUpcomingSchedules(1L, fromTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(scheduleService, times(1)).getUpcomingSchedules(1L, fromTime);
    }

    /**
     * Tests schedule update via PUT endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testUpdateSchedule_Success() {
        // Arrange
        UpdateScheduleRequest request = new UpdateScheduleRequest();
        request.setDepartureTime(LocalTime.of(9, 0));
        when(scheduleService.updateSchedule(anyLong(), any(UpdateScheduleRequest.class))).thenReturn(scheduleResponse);

        // Act
        ResponseEntity<ScheduleResponse> response = scheduleController.updateSchedule(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(scheduleService, times(1)).updateSchedule(1L, request);
    }

    /**
     * Tests schedule deletion via DELETE endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeleteSchedule_Success() {
        // Arrange
        doNothing().when(scheduleService).deleteSchedule(anyLong());

        // Act
        ResponseEntity<Void> response = scheduleController.deleteSchedule(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(scheduleService, times(1)).deleteSchedule(1L);
    }

    /**
     * Tests schedule activation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testActivateSchedule_Success() {
        // Arrange
        doNothing().when(scheduleService).activateSchedule(anyLong());

        // Act
        ResponseEntity<Void> response = scheduleController.activateSchedule(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(scheduleService, times(1)).activateSchedule(1L);
    }

    /**
     * Tests schedule deactivation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeactivateSchedule_Success() {
        // Arrange
        doNothing().when(scheduleService).deactivateSchedule(anyLong());

        // Act
        ResponseEntity<Void> response = scheduleController.deactivateSchedule(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(scheduleService, times(1)).deactivateSchedule(1L);
    }
}

