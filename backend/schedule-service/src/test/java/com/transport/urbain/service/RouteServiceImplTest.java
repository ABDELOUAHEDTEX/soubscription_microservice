package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.RouteMapper;
import com.transport.urbain.dto.request.AddRouteStopRequest;
import com.transport.urbain.dto.request.CreateRouteRequest;
import com.transport.urbain.dto.response.RouteDetailsResponse;
import com.transport.urbain.dto.response.RouteResponse;
import com.transport.urbain.event.RouteChangedEvent;
import com.transport.urbain.event.producer.ScheduleEventProducer;
import com.transport.urbain.exception.DuplicateRouteException;
import com.transport.urbain.exception.RouteNotFoundException;
import com.transport.urbain.exception.StopNotFoundException;
import com.transport.urbain.model.Route;
import com.transport.urbain.model.RouteStop;
import com.transport.urbain.model.Stop;
import com.transport.urbain.repository.RouteRepository;
import com.transport.urbain.repository.RouteStopRepository;
import com.transport.urbain.repository.StopRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RouteServiceImpl.
 * <p>
 * This test class covers all business logic in RouteServiceImpl including:
 * <ul>
 *     <li>Route creation with validation</li>
 *     <li>Route retrieval by ID and number</li>
 *     <li>Route listing with pagination</li>
 *     <li>Route update operations</li>
 *     <li>Route deletion</li>
 *     <li>Route stop management (add/remove)</li>
 *     <li>Route activation/deactivation</li>
 *     <li>Exception handling</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private ScheduleEventProducer scheduleEventProducer;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route testRoute;
    private CreateRouteRequest createRouteRequest;
    private RouteResponse routeResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for Route, CreateRouteRequest, and RouteResponse.
     */
    @BeforeEach
    void setUp() {
        testRoute = Route.builder()
                .id(1L)
                .routeNumber("R101")
                .routeName("Downtown Express")
                .description("Fast service to downtown")
                .origin("City Center")
                .destination("Downtown Mall")
                .distance(new BigDecimal("15.5"))
                .estimatedDuration(30)
                .isActive(true)
                .isCircular(false)
                .color("#FF5733")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRouteRequest = new CreateRouteRequest();
        createRouteRequest.setRouteNumber("R101");
        createRouteRequest.setRouteName("Downtown Express");
        createRouteRequest.setDescription("Fast service to downtown");
        createRouteRequest.setOrigin("City Center");
        createRouteRequest.setDestination("Downtown Mall");
        createRouteRequest.setDistance(new BigDecimal("15.5"));
        createRouteRequest.setEstimatedDuration(30);
        createRouteRequest.setIsCircular(false);
        createRouteRequest.setColor("#FF5733");

        routeResponse = new RouteResponse();
        routeResponse.setId(1L);
        routeResponse.setRouteNumber("R101");
        routeResponse.setRouteName("Downtown Express");
        routeResponse.setIsActive(true);
    }

    /**
     * Tests successful route creation.
     * Verifies that a new route is created when valid data is provided.
     */
    @Test
    void testCreateRoute_Success() {
        // Arrange
        when(routeRepository.existsByRouteNumber(anyString())).thenReturn(false);
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

        // Act
        RouteResponse result = routeService.createRoute(createRouteRequest);

        // Assert
        assertNotNull(result);
        assertEquals("R101", result.getRouteNumber());
        verify(routeRepository, times(1)).existsByRouteNumber("R101");
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    /**
     * Tests route creation failure when route number already exists.
     * Verifies that DuplicateRouteException is thrown.
     */
    @Test
    void testCreateRoute_DuplicateRouteNumber() {
        // Arrange
        when(routeRepository.existsByRouteNumber("R101")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateRouteException.class, () -> routeService.createRoute(createRouteRequest));
        verify(routeRepository, times(1)).existsByRouteNumber("R101");
        verify(routeRepository, never()).save(any());
    }

    /**
     * Tests successful route retrieval by ID.
     * Verifies that a route is retrieved when a valid ID is provided.
     */
    @Test
    void testGetRouteById_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

        // Act
        RouteResponse result = routeService.getRouteById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(routeRepository, times(1)).findById(1L);
    }

    /**
     * Tests route retrieval failure when route ID doesn't exist.
     * Verifies that RouteNotFoundException is thrown.
     */
    @Test
    void testGetRouteById_NotFound() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RouteNotFoundException.class, () -> routeService.getRouteById(999L));
        verify(routeRepository, times(1)).findById(999L);
    }

    /**
     * Tests successful route retrieval by route number.
     * Verifies that a route is retrieved when a valid route number is provided.
     */
    @Test
    void testGetRouteByNumber_Success() {
        // Arrange
        when(routeRepository.findByRouteNumber("R101")).thenReturn(Optional.of(testRoute));
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

        // Act
        RouteResponse result = routeService.getRouteByNumber("R101");

        // Assert
        assertNotNull(result);
        verify(routeRepository, times(1)).findByRouteNumber("R101");
    }

    /**
     * Tests retrieval of all routes with pagination.
     * Verifies that a page of routes is returned correctly.
     */
    @Test
    void testGetAllRoutes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Route> routePage = new PageImpl<>(List.of(testRoute));
        when(routeRepository.findAll(pageable)).thenReturn(routePage);
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

        // Act
        Page<RouteResponse> result = routeService.getAllRoutes(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(routeRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests successful route update operation.
     * Verifies that route information is updated correctly.
     */
    @Test
    void testUpdateRoute_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);
        doNothing().when(scheduleEventProducer).publishRouteChanged(any(RouteChangedEvent.class));

        // Act
        RouteResponse result = routeService.updateRoute(1L, createRouteRequest);

        // Assert
        assertNotNull(result);
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(any(Route.class));
        verify(scheduleEventProducer, times(1)).publishRouteChanged(any(RouteChangedEvent.class));
    }

    /**
     * Tests route update failure when route ID doesn't exist.
     * Verifies that RouteNotFoundException is thrown.
     */
    @Test
    void testUpdateRoute_NotFound() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RouteNotFoundException.class, () -> routeService.updateRoute(999L, createRouteRequest));
        verify(routeRepository, times(1)).findById(999L);
        verify(routeRepository, never()).save(any());
    }

    /**
     * Tests successful route deletion.
     * Verifies that a route is deleted when a valid ID is provided.
     */
    @Test
    void testDeleteRoute_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        doNothing().when(routeRepository).delete(any(Route.class));

        // Act
        assertDoesNotThrow(() -> routeService.deleteRoute(1L));

        // Assert
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).delete(testRoute);
    }

    /**
     * Tests successful addition of stop to route.
     * Verifies that a stop is added to a route correctly.
     */
    @Test
    void testAddStopToRoute_Success() {
        // Arrange
        Stop testStop = Stop.builder()
                .id(1L)
                .stopCode("ST001")
                .stopName("Central Station")
                .build();

        AddRouteStopRequest request = new AddRouteStopRequest();
        request.setStopId(1L);
        request.setSequenceNumber(1);
        request.setDistanceFromOrigin(new BigDecimal("0.0"));
        request.setTimeFromOrigin(0);
        request.setDwellTime(1);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        when(routeStopRepository.save(any(RouteStop.class))).thenReturn(new RouteStop());

        // Act
        assertDoesNotThrow(() -> routeService.addStopToRoute(1L, request));

        // Assert
        verify(routeRepository, times(1)).findById(1L);
        verify(stopRepository, times(1)).findById(1L);
        verify(routeStopRepository, times(1)).save(any(RouteStop.class));
    }

    /**
     * Tests addition of stop to route failure when route doesn't exist.
     * Verifies that RouteNotFoundException is thrown.
     */
    @Test
    void testAddStopToRoute_RouteNotFound() {
        // Arrange
        AddRouteStopRequest request = new AddRouteStopRequest();
        request.setStopId(1L);

        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RouteNotFoundException.class, () -> routeService.addStopToRoute(999L, request));
        verify(routeRepository, times(1)).findById(999L);
    }

    /**
     * Tests addition of stop to route failure when stop doesn't exist.
     * Verifies that StopNotFoundException is thrown.
     */
    @Test
    void testAddStopToRoute_StopNotFound() {
        // Arrange
        AddRouteStopRequest request = new AddRouteStopRequest();
        request.setStopId(999L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StopNotFoundException.class, () -> routeService.addStopToRoute(1L, request));
        verify(stopRepository, times(1)).findById(999L);
    }

    /**
     * Tests successful removal of stop from route.
     * Verifies that a stop is removed from a route correctly.
     */
    @Test
    void testRemoveStopFromRoute_Success() {
        // Arrange
        doNothing().when(routeStopRepository).deleteByRouteIdAndStopId(anyLong(), anyLong());

        // Act
        assertDoesNotThrow(() -> routeService.removeStopFromRoute(1L, 1L));

        // Assert
        verify(routeStopRepository, times(1)).deleteByRouteIdAndStopId(1L, 1L);
    }

    /**
     * Tests successful route activation.
     * Verifies that a route is activated correctly.
     */
    @Test
    void testActivateRoute_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // Act
        assertDoesNotThrow(() -> routeService.activateRoute(1L));

        // Assert
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    /**
     * Tests successful route deactivation.
     * Verifies that a route is deactivated correctly.
     */
    @Test
    void testDeactivateRoute_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // Act
        assertDoesNotThrow(() -> routeService.deactivateRoute(1L));

        // Assert
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    /**
     * Tests retrieval of route details with stops.
     * Verifies that detailed route information is returned.
     */
    @Test
    void testGetRouteDetails_Success() {
        // Arrange
        RouteStop routeStop = RouteStop.builder()
                .id(1L)
                .stop(Stop.builder()
                        .id(1L)
                        .stopCode("ST001")
                        .stopName("Central Station")
                        .latitude(new BigDecimal("12.34567890"))
                        .longitude(new BigDecimal("98.76543210"))
                        .build())
                .sequenceNumber(1)
                .distanceFromOrigin(new BigDecimal("0.0"))
                .timeFromOrigin(0)
                .dwellTime(1)
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeStopRepository.findByRouteIdOrderBySequenceNumberAsc(1L))
                .thenReturn(List.of(routeStop));

        // Act
        RouteDetailsResponse result = routeService.getRouteDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals("R101", result.getRouteNumber());
        assertEquals(1, result.getStops().size());
        verify(routeRepository, times(1)).findById(1L);
    }

    /**
     * Tests route search functionality.
     * Verifies that routes matching the search keyword are returned.
     */
    @Test
    void testSearchRoutes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Route> routePage = new PageImpl<>(List.of(testRoute));
        when(routeRepository.searchRoutes("Downtown", pageable)).thenReturn(routePage);
        when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

        // Act
        Page<RouteResponse> result = routeService.searchRoutes("Downtown", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(routeRepository, times(1)).searchRoutes("Downtown", pageable);
    }
}

