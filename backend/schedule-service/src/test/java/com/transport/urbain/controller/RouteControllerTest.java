package com.transport.urbain.controller;

import com.transport.urbain.dto.request.AddRouteStopRequest;
import com.transport.urbain.dto.request.CreateRouteRequest;
import com.transport.urbain.dto.response.RouteDetailsResponse;
import com.transport.urbain.dto.response.RouteResponse;
import com.transport.urbain.service.RouteService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RouteController.
 * <p>
 * This test class covers all REST endpoints in RouteController including:
 * <ul>
 *     <li>Route creation and retrieval</li>
 *     <li>Route update and deletion</li>
 *     <li>Stop management (add/remove)</li>
 *     <li>Route activation/deactivation</li>
 *     <li>Response status codes and body validation</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock
    private RouteService routeService;

    @InjectMocks
    private RouteController routeController;

    private CreateRouteRequest createRouteRequest;
    private RouteResponse routeResponse;
    private RouteDetailsResponse routeDetailsResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for CreateRouteRequest and RouteResponse.
     */
    @BeforeEach
    void setUp() {
        createRouteRequest = new CreateRouteRequest();
        createRouteRequest.setRouteNumber("R101");
        createRouteRequest.setRouteName("Downtown Express");
        createRouteRequest.setDistance(new BigDecimal("15.5"));

        routeResponse = new RouteResponse();
        routeResponse.setId(1L);
        routeResponse.setRouteNumber("R101");
        routeResponse.setIsActive(true);

        routeDetailsResponse = new RouteDetailsResponse();
        routeDetailsResponse.setId(1L);
        routeDetailsResponse.setRouteNumber("R101");
    }

    /**
     * Tests successful route creation via POST endpoint.
     * Verifies that HTTP 201 status is returned.
     */
    @Test
    void testCreateRoute_Success() {
        // Arrange
        when(routeService.createRoute(any(CreateRouteRequest.class))).thenReturn(routeResponse);

        // Act
        ResponseEntity<RouteResponse> response = routeController.createRoute(createRouteRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("R101", response.getBody().getRouteNumber());
        verify(routeService, times(1)).createRoute(any(CreateRouteRequest.class));
    }

    /**
     * Tests retrieval of route by ID via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetRouteById_Success() {
        // Arrange
        when(routeService.getRouteById(1L)).thenReturn(routeResponse);

        // Act
        ResponseEntity<RouteResponse> response = routeController.getRouteById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(routeService, times(1)).getRouteById(1L);
    }

    /**
     * Tests retrieval of route by route number via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetRouteByNumber_Success() {
        // Arrange
        when(routeService.getRouteByNumber("R101")).thenReturn(routeResponse);

        // Act
        ResponseEntity<RouteResponse> response = routeController.getRouteByNumber("R101");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(routeService, times(1)).getRouteByNumber("R101");
    }

    /**
     * Tests retrieval of route details via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetRouteDetails_Success() {
        // Arrange
        when(routeService.getRouteDetails(1L)).thenReturn(routeDetailsResponse);

        // Act
        ResponseEntity<RouteDetailsResponse> response = routeController.getRouteDetails(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(routeService, times(1)).getRouteDetails(1L);
    }

    /**
     * Tests retrieval of all routes via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetAllRoutes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<RouteResponse> routePage = new PageImpl<>(List.of(routeResponse));
        when(routeService.getAllRoutes(pageable)).thenReturn(routePage);

        // Act
        ResponseEntity<Page<RouteResponse>> response = routeController.getAllRoutes(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(routeService, times(1)).getAllRoutes(pageable);
    }

    /**
     * Tests retrieval of active routes via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetActiveRoutes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<RouteResponse> routePage = new PageImpl<>(List.of(routeResponse));
        when(routeService.getActiveRoutes(pageable)).thenReturn(routePage);

        // Act
        ResponseEntity<Page<RouteResponse>> response = routeController.getActiveRoutes(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(routeService, times(1)).getActiveRoutes(pageable);
    }

    /**
     * Tests route search via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testSearchRoutes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<RouteResponse> routePage = new PageImpl<>(List.of(routeResponse));
        when(routeService.searchRoutes("Downtown", pageable)).thenReturn(routePage);

        // Act
        ResponseEntity<Page<RouteResponse>> response = routeController.searchRoutes("Downtown", pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(routeService, times(1)).searchRoutes("Downtown", pageable);
    }

    /**
     * Tests route update via PUT endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testUpdateRoute_Success() {
        // Arrange
        when(routeService.updateRoute(anyLong(), any(CreateRouteRequest.class))).thenReturn(routeResponse);

        // Act
        ResponseEntity<RouteResponse> response = routeController.updateRoute(1L, createRouteRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(routeService, times(1)).updateRoute(1L, createRouteRequest);
    }

    /**
     * Tests route deletion via DELETE endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeleteRoute_Success() {
        // Arrange
        doNothing().when(routeService).deleteRoute(anyLong());

        // Act
        ResponseEntity<Void> response = routeController.deleteRoute(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(routeService, times(1)).deleteRoute(1L);
    }

    /**
     * Tests adding stop to route via POST endpoint.
     * Verifies that HTTP 201 status is returned.
     */
    @Test
    void testAddStopToRoute_Success() {
        // Arrange
        AddRouteStopRequest request = new AddRouteStopRequest();
        request.setStopId(1L);
        request.setSequenceNumber(1);
        doNothing().when(routeService).addStopToRoute(anyLong(), any(AddRouteStopRequest.class));

        // Act
        ResponseEntity<Void> response = routeController.addStopToRoute(1L, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(routeService, times(1)).addStopToRoute(1L, request);
    }

    /**
     * Tests removing stop from route via DELETE endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testRemoveStopFromRoute_Success() {
        // Arrange
        doNothing().when(routeService).removeStopFromRoute(anyLong(), anyLong());

        // Act
        ResponseEntity<Void> response = routeController.removeStopFromRoute(1L, 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(routeService, times(1)).removeStopFromRoute(1L, 1L);
    }

    /**
     * Tests route activation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testActivateRoute_Success() {
        // Arrange
        doNothing().when(routeService).activateRoute(anyLong());

        // Act
        ResponseEntity<Void> response = routeController.activateRoute(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(routeService, times(1)).activateRoute(1L);
    }

    /**
     * Tests route deactivation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeactivateRoute_Success() {
        // Arrange
        doNothing().when(routeService).deactivateRoute(anyLong());

        // Act
        ResponseEntity<Void> response = routeController.deactivateRoute(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(routeService, times(1)).deactivateRoute(1L);
    }
}

