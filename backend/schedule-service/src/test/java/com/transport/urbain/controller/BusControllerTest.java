package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateBusRequest;
import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.model.BusStatus;
import com.transport.urbain.service.BusService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BusController.
 * <p>
 * This test class covers all REST endpoints in BusController including:
 * <ul>
 *     <li>Bus creation endpoints</li>
 *     <li>Bus retrieval endpoints</li>
 *     <li>Bus update endpoints</li>
 *     <li>Bus deletion endpoints</li>
 *     <li>Bus status management endpoints</li>
 *     <li>Response status codes and body validation</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class BusControllerTest {

    @Mock
    private BusService busService;

    @InjectMocks
    private BusController busController;

    private CreateBusRequest createBusRequest;
    private BusResponse busResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for CreateBusRequest and BusResponse.
     */
    @BeforeEach
    void setUp() {
        createBusRequest = new CreateBusRequest();
        createBusRequest.setBusNumber("BUS-001");
        createBusRequest.setLicensePlate("ABC-123");

        busResponse = new BusResponse();
        busResponse.setId(1L);
        busResponse.setBusNumber("BUS-001");
        busResponse.setStatus(BusStatus.ACTIVE);
    }

    /**
     * Tests successful bus creation via POST endpoint.
     * Verifies that HTTP 201 status is returned.
     */
    @Test
    void testCreateBus_Success() {
        // Arrange
        when(busService.createBus(any(CreateBusRequest.class))).thenReturn(busResponse);

        // Act
        ResponseEntity<BusResponse> response = busController.createBus(createBusRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BUS-001", response.getBody().getBusNumber());
        verify(busService, times(1)).createBus(any(CreateBusRequest.class));
    }

    /**
     * Tests retrieval of bus by ID via GET endpoint.
     * Verifies that HTTP 200 status and bus data is returned.
     */
    @Test
    void testGetBusById_Success() {
        // Arrange
        when(busService.getBusById(1L)).thenReturn(busResponse);

        // Act
        ResponseEntity<BusResponse> response = busController.getBusById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(busService, times(1)).getBusById(1L);
    }

    /**
     * Tests retrieval of bus by bus number via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetBusByNumber_Success() {
        // Arrange
        when(busService.getBusByNumber("BUS-001")).thenReturn(busResponse);

        // Act
        ResponseEntity<BusResponse> response = busController.getBusByNumber("BUS-001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(busService, times(1)).getBusByNumber("BUS-001");
    }

    /**
     * Tests retrieval of all buses with pagination via GET endpoint.
     * Verifies that HTTP 200 status and page data is returned.
     */
    @Test
    void testGetAllBuses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<BusResponse> busPage = new PageImpl<>(List.of(busResponse));
        when(busService.getAllBuses(pageable)).thenReturn(busPage);

        // Act
        ResponseEntity<Page<BusResponse>> response = busController.getAllBuses(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(busService, times(1)).getAllBuses(pageable);
    }

    /**
     * Tests retrieval of available buses via GET endpoint.
     * Verifies that HTTP 200 status and list data is returned.
     */
    @Test
    void testGetAvailableBuses_Success() {
        // Arrange
        when(busService.getAvailableBuses()).thenReturn(List.of(busResponse));

        // Act
        ResponseEntity<List<BusResponse>> response = busController.getAvailableBuses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(busService, times(1)).getAvailableBuses();
    }

    /**
     * Tests retrieval of buses by status via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetBusesByStatus_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<BusResponse> busPage = new PageImpl<>(List.of(busResponse));
        when(busService.getBusesByStatus(BusStatus.ACTIVE, pageable)).thenReturn(busPage);

        // Act
        ResponseEntity<Page<BusResponse>> response = busController.getBusesByStatus(BusStatus.ACTIVE, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(busService, times(1)).getBusesByStatus(BusStatus.ACTIVE, pageable);
    }

    /**
     * Tests bus update via PUT endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testUpdateBus_Success() {
        // Arrange
        when(busService.updateBus(anyLong(), any(CreateBusRequest.class))).thenReturn(busResponse);

        // Act
        ResponseEntity<BusResponse> response = busController.updateBus(1L, createBusRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(busService, times(1)).updateBus(1L, createBusRequest);
    }

    /**
     * Tests bus deletion via DELETE endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeleteBus_Success() {
        // Arrange
        doNothing().when(busService).deleteBus(anyLong());

        // Act
        ResponseEntity<Void> response = busController.deleteBus(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(busService, times(1)).deleteBus(1L);
    }

    /**
     * Tests bus status update via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testUpdateBusStatus_Success() {
        // Arrange
        doNothing().when(busService).updateBusStatus(anyLong(), any(BusStatus.class));

        // Act
        ResponseEntity<Void> response = busController.updateBusStatus(1L, BusStatus.MAINTENANCE);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(busService, times(1)).updateBusStatus(1L, BusStatus.MAINTENANCE);
    }
}

