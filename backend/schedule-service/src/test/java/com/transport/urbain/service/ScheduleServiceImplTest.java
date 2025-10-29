package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.ScheduleMapper;
import com.transport.urbain.dto.request.CreateScheduleRequest;
import com.transport.urbain.dto.request.SearchScheduleRequest;
import com.transport.urbain.dto.request.UpdateScheduleRequest;
import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.event.ScheduleCreatedEvent;
import com.transport.urbain.event.ScheduleUpdatedEvent;
import com.transport.urbain.event.producer.ScheduleEventProducer;
import com.transport.urbain.exception.InvalidScheduleException;
import com.transport.urbain.exception.RouteNotFoundException;
import com.transport.urbain.exception.ScheduleNotFoundException;
import com.transport.urbain.model.*;
import com.transport.urbain.repository.BusRepository;
import com.transport.urbain.repository.RouteRepository;
import com.transport.urbain.repository.ScheduleRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduleServiceImpl.
 * <p>
 * This test class covers all business logic in ScheduleServiceImpl including:
 * <ul>
 *     <li>Schedule creation with validation</li>
 *     <li>Schedule retrieval by ID and route</li>
 *     <li>Schedule listing with pagination</li>
 *     <li>Schedule update operations</li>
 *     <li>Schedule deletion</li>
 *     <li>Schedule activation/deactivation</li>
 *     <li>Time-based queries</li>
 *     <li>Exception handling</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private ScheduleEventProducer scheduleEventProducer;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Route testRoute;
    private Bus testBus;
    private Schedule testSchedule;
    private CreateScheduleRequest createScheduleRequest;
    private ScheduleResponse scheduleResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for Route, Bus, Schedule, and related DTOs.
     */
    @BeforeEach
    void setUp() {
        testRoute = Route.builder()
                .id(1L)
                .routeNumber("R101")
                .routeName("Downtown Express")
                .isActive(true)
                .build();

        testBus = Bus.builder()
                .id(1L)
                .busNumber("BUS-001")
                .status(BusStatus.ACTIVE)
                .build();

        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        daysOfWeek.add(DayOfWeek.MONDAY);
        daysOfWeek.add(DayOfWeek.WEDNESDAY);

        testSchedule = Schedule.builder()
                .id(1L)
                .route(testRoute)
                .bus(testBus)
                .departureTime(LocalTime.of(8, 0))
                .arrivalTime(LocalTime.of(9, 30))
                .scheduleType(ScheduleType.REGULAR)
                .daysOfWeek(daysOfWeek)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusMonths(3))
                .isActive(true)
                .frequency(60)
                .build();

        createScheduleRequest = new CreateScheduleRequest();
        createScheduleRequest.setRouteId(1L);
        createScheduleRequest.setBusId(1L);
        createScheduleRequest.setDepartureTime(LocalTime.of(8, 0));
        createScheduleRequest.setArrivalTime(LocalTime.of(9, 30));
        createScheduleRequest.setScheduleType(ScheduleType.REGULAR);
        createScheduleRequest.setDaysOfWeek(daysOfWeek);
        createScheduleRequest.setValidFrom(LocalDate.now());
        createScheduleRequest.setValidUntil(LocalDate.now().plusMonths(3));
        createScheduleRequest.setFrequency(60);

        scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(1L);
        scheduleResponse.setRouteId(1L);
        scheduleResponse.setScheduleType(ScheduleType.REGULAR);
        scheduleResponse.setIsActive(true);
    }

    /**
     * Tests successful schedule creation.
     * Verifies that a new schedule is created when valid data is provided.
     */
    @Test
    void testCreateSchedule_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);
        doNothing().when(scheduleEventProducer).publishScheduleCreated(any(ScheduleCreatedEvent.class));

        // Act
        ScheduleResponse result = scheduleService.createSchedule(createScheduleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(routeRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
        verify(scheduleEventProducer, times(1)).publishScheduleCreated(any(ScheduleCreatedEvent.class));
    }

    /**
     * Tests schedule creation failure when departure time is after arrival time.
     * Verifies that InvalidScheduleException is thrown.
     */
    @Test
    void testCreateSchedule_InvalidTimeSequence() {
        // Arrange
        createScheduleRequest.setDepartureTime(LocalTime.of(10, 0));
        createScheduleRequest.setArrivalTime(LocalTime.of(9, 0)); // Invalid: arrival before departure

        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));

        // Act & Assert
        assertThrows(InvalidScheduleException.class, () -> scheduleService.createSchedule(createScheduleRequest));
        verify(scheduleRepository, never()).save(any());
    }

    /**
     * Tests schedule creation failure when route doesn't exist.
     * Verifies that RouteNotFoundException is thrown.
     */
    @Test
    void testCreateSchedule_RouteNotFound() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RouteNotFoundException.class, () -> scheduleService.createSchedule(createScheduleRequest));
        verify(routeRepository, times(1)).findById(1L);
        verify(scheduleRepository, never()).save(any());
    }

    /**
     * Tests successful schedule retrieval by ID.
     * Verifies that a schedule is retrieved when a valid ID is provided.
     */
    @Test
    void testGetScheduleById_Success() {
        // Arrange
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        ScheduleResponse result = scheduleService.getScheduleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(scheduleRepository, times(1)).findById(1L);
    }

    /**
     * Tests schedule retrieval failure when schedule ID doesn't exist.
     * Verifies that ScheduleNotFoundException is thrown.
     */
    @Test
    void testGetScheduleById_NotFound() {
        // Arrange
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getScheduleById(999L));
        verify(scheduleRepository, times(1)).findById(999L);
    }

    /**
     * Tests retrieval of schedules by route.
     * Verifies that all schedules for a specific route are returned.
     */
    @Test
    void testGetSchedulesByRoute_Success() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Schedule> schedulePage = new PageImpl<>(List.of(testSchedule));
        when(scheduleRepository.findByRouteId(1L, pageable)).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        List<ScheduleResponse> result = scheduleService.getSchedulesByRoute(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository, times(1)).findByRouteId(1L, pageable);
    }

    /**
     * Tests successful schedule update operation.
     * Verifies that schedule information is updated correctly.
     */
    @Test
    void testUpdateSchedule_Success() {
        // Arrange
        UpdateScheduleRequest updateRequest = new UpdateScheduleRequest();
        updateRequest.setDepartureTime(LocalTime.of(9, 0));
        updateRequest.setArrivalTime(LocalTime.of(10, 30));
        updateRequest.setIsActive(false);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);
        doNothing().when(scheduleEventProducer).publishScheduleUpdated(any(ScheduleUpdatedEvent.class));

        // Act
        ScheduleResponse result = scheduleService.updateSchedule(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
        verify(scheduleEventProducer, times(1)).publishScheduleUpdated(any(ScheduleUpdatedEvent.class));
    }

    /**
     * Tests schedule update failure when schedule ID doesn't exist.
     * Verifies that ScheduleNotFoundException is thrown.
     */
    @Test
    void testUpdateSchedule_NotFound() {
        // Arrange
        UpdateScheduleRequest updateRequest = new UpdateScheduleRequest();
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.updateSchedule(999L, updateRequest));
        verify(scheduleRepository, times(1)).findById(999L);
        verify(scheduleRepository, never()).save(any());
    }

    /**
     * Tests successful schedule deletion.
     * Verifies that a schedule is deleted when a valid ID is provided.
     */
    @Test
    void testDeleteSchedule_Success() {
        // Arrange
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        doNothing().when(scheduleRepository).delete(any(Schedule.class));

        // Act
        assertDoesNotThrow(() -> scheduleService.deleteSchedule(1L));

        // Assert
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).delete(testSchedule);
    }

    /**
     * Tests schedule deletion failure when schedule ID doesn't exist.
     * Verifies that ScheduleNotFoundException is thrown.
     */
    @Test
    void testDeleteSchedule_NotFound() {
        // Arrange
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.deleteSchedule(999L));
        verify(scheduleRepository, times(1)).findById(999L);
        verify(scheduleRepository, never()).delete(any());
    }

    /**
     * Tests successful schedule activation.
     * Verifies that a schedule is activated correctly.
     */
    @Test
    void testActivateSchedule_Success() {
        // Arrange
        testSchedule.setIsActive(false);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // Act
        assertDoesNotThrow(() -> scheduleService.activateSchedule(1L));

        // Assert
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    /**
     * Tests successful schedule deactivation.
     * Verifies that a schedule is deactivated correctly.
     */
    @Test
    void testDeactivateSchedule_Success() {
        // Arrange
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // Act
        assertDoesNotThrow(() -> scheduleService.deactivateSchedule(1L));

        // Assert
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    /**
     * Tests retrieval of today's schedules for a specific route.
     * Verifies that schedules for today are returned.
     */
    @Test
    void testGetTodaySchedules_Success() {
        // Arrange
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(today.getDayOfWeek().name());
        when(scheduleRepository.findActiveSchedulesForRouteAndDate(1L, today, dayOfWeek))
                .thenReturn(List.of(testSchedule));
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        List<ScheduleResponse> result = scheduleService.getTodaySchedules(1L);

        // Assert
        assertNotNull(result);
        verify(scheduleRepository, times(1))
                .findActiveSchedulesForRouteAndDate(1L, today, dayOfWeek);
    }

    /**
     * Tests retrieval of upcoming schedules for a specific route.
     * Verifies that schedules after a given time are returned.
     */
    @Test
    void testGetUpcomingSchedules_Success() {
        // Arrange
        LocalTime fromTime = LocalTime.of(7, 0); // Before departure time of 8:0
        Pageable pageable = Pageable.unpaged();
        Page<Schedule> schedulePage = new PageImpl<>(List.of(testSchedule));
        when(scheduleRepository.findByRouteId(1L, pageable)).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        List<ScheduleResponse> result = scheduleService.getUpcomingSchedules(1L, fromTime);

        // Assert
        assertNotNull(result);
        verify(scheduleRepository, times(1)).findByRouteId(1L, pageable);
    }

    /**
     * Tests schedule search functionality.
     * Verifies that schedules matching the search criteria are returned.
     */
    @Test
    void testSearchSchedules_Success() {
        // Arrange
        SearchScheduleRequest request = new SearchScheduleRequest();
        request.setRouteId(1L);

        Pageable pageable = Pageable.unpaged();
        Page<Schedule> schedulePage = new PageImpl<>(List.of(testSchedule));
        when(scheduleRepository.findByRouteId(1L, pageable)).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        List<ScheduleResponse> result = scheduleService.searchSchedules(request);

        // Assert
        assertNotNull(result);
        verify(scheduleRepository, times(1)).findByRouteId(1L, pageable);
    }

    /**
     * Tests retrieval of all schedules with pagination.
     * Verifies that a page of schedules is returned correctly.
     */
    @Test
    void testGetAllSchedules_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Schedule> schedulePage = new PageImpl<>(List.of(testSchedule));
        when(scheduleRepository.findAll(pageable)).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        Page<ScheduleResponse> result = scheduleService.getAllSchedules(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(scheduleRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests retrieval of active schedules with pagination.
     * Verifies that a page of active schedules is returned correctly.
     */
    @Test
    void testGetActiveSchedules_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Schedule> schedulePage = new PageImpl<>(List.of(testSchedule));
        when(scheduleRepository.findByIsActive(true, pageable)).thenReturn(schedulePage);
        when(scheduleMapper.toScheduleResponse(any(Schedule.class))).thenReturn(scheduleResponse);

        // Act
        Page<ScheduleResponse> result = scheduleService.getActiveSchedules(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(scheduleRepository, times(1)).findByIsActive(true, pageable);
    }
}

