package com.transport.urbain.controller;

import com.transport.urbain.dto.request.CreateStopRequest;
import com.transport.urbain.dto.response.StopResponse;
import com.transport.urbain.service.StopService;
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
 * Unit tests for StopController.
 * <p>
 * This test class covers all REST endpoints in StopController including:
 * <ul>
 *     <li>Stop creation and retrieval</li>
 *     <li>Stop update and deletion</li>
 *     <li>Stop search functionality</li>
 *     <li>Geospatial queries (nearby stops)</li>
 *     <li>Stop activation/deactivation</li>
 *     <li>Response status codes and body validation</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class StopControllerTest {

    @Mock
    private StopService stopService;

    @InjectMocks
    private StopController stopController;

    private CreateStopRequest createStopRequest;
    private StopResponse stopResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for CreateStopRequest and StopResponse.
     */
    @BeforeEach
    void setUp() {
        createStopRequest = new CreateStopRequest();
        createStopRequest.setStopCode("ST001");
        createStopRequest.setStopName("Central Station");
        createStopRequest.setLatitude(new BigDecimal("12.34567890"));
        createStopRequest.setLongitude(new BigDecimal("98.76543210"));

        stopResponse = new StopResponse();
        stopResponse.setId(1L);
        stopResponse.setStopCode("ST001");
        stopResponse.setStopName("Central Station");
        stopResponse.setIsActive(true);
    }

    /**
     * Tests successful stop creation via POST endpoint.
     * Verifies that HTTP 201 status is returned.
     */
    @Test
    void testCreateStop_Success() {
        // Arrange
        when(stopService.createStop(any(CreateStopRequest.class))).thenReturn(stopResponse);

        // Act
        ResponseEntity<StopResponse> response = stopController.createStop(createStopRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ST001", response.getBody().getStopCode());
        verify(stopService, times(1)).createStop(any(CreateStopRequest.class));
    }

    /**
     * Tests retrieval of stop by ID via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetStopById_Success() {
        // Arrange
        when(stopService.getStopById(1L)).thenReturn(stopResponse);

        // Act
        ResponseEntity<StopResponse> response = stopController.getStopById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(stopService, times(1)).getStopById(1L);
    }

    /**
     * Tests retrieval of stop by code via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetStopByCode_Success() {
        // Arrange
        when(stopService.getStopByCode("ST001")).thenReturn(stopResponse);

        // Act
        ResponseEntity<StopResponse> response = stopController.getStopByCode("ST001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(stopService, times(1)).getStopByCode("ST001");
    }

    /**
     * Tests retrieval of all stops via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetAllStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<StopResponse> stopPage = new PageImpl<>(List.of(stopResponse));
        when(stopService.getAllStops(pageable)).thenReturn(stopPage);

        // Act
        ResponseEntity<Page<StopResponse>> response = stopController.getAllStops(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(stopService, times(1)).getAllStops(pageable);
    }

    /**
     * Tests retrieval of active stops via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetActiveStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<StopResponse> stopPage = new PageImpl<>(List.of(stopResponse));
        when(stopService.getActiveStops(pageable)).thenReturn(stopPage);

        // Act
        ResponseEntity<Page<StopResponse>> response = stopController.getActiveStops(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(stopService, times(1)).getActiveStops(pageable);
    }

    /**
     * Tests stop search via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testSearchStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<StopResponse> stopPage = new PageImpl<>(List.of(stopResponse));
        when(stopService.searchStops("Central", pageable)).thenReturn(stopPage);

        // Act
        ResponseEntity<Page<StopResponse>> response = stopController.searchStops("Central", pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(stopService, times(1)).searchStops("Central", pageable);
    }

    /**
     * Tests retrieval of nearby stops via GET endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testGetNearbyStops_Success() {
        // Arrange
        BigDecimal latitude = new BigDecimal("12.34567890");
        BigDecimal longitude = new BigDecimal("98.76543210");
        Double radius = 1.0;
        
        when(stopService.getNearbyStops(latitude, longitude, radius))
                .thenReturn(List.of(stopResponse));

        // Act
        ResponseEntity<List<StopResponse>> response = stopController.getNearbyStops(latitude, longitude, radius);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(stopService, times(1)).getNearbyStops(latitude, longitude, radius);
    }

    /**
     * Tests retrieval of nearby stops with default radius.
     * Verifies that HTTP 200 status is returned with default radius.
     */
    @Test
    void testGetNearbyStops_WithDefaultRadius() {
        // Arrange
        BigDecimal latitude = new BigDecimal("12.34567890");
        BigDecimal longitude = new BigDecimal("98.76543210");
        Double defaultRadius = 1.0;
        
        when(stopService.getNearbyStops(latitude, longitude, defaultRadius))
                .thenReturn(List.of(stopResponse));

        // Act
        ResponseEntity<List<StopResponse>> response = stopController.getNearbyStops(latitude, longitude, defaultRadius);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(stopService, times(1)).getNearbyStops(latitude, longitude, defaultRadius);
    }

    /**
     * Tests stop update via PUT endpoint.
     * Verifies that HTTP 200 status is returned.
     */
    @Test
    void testUpdateStop_Success() {
        // Arrange
        when(stopService.updateStop(anyLong(), any(CreateStopRequest.class))).thenReturn(stopResponse);

        // Act
        ResponseEntity<StopResponse> response = stopController.updateStop(1L, createStopRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(stopService, times(1)).updateStop(1L, createStopRequest);
    }

    /**
     * Tests stop deletion via DELETE endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeleteStop_Success() {
        // Arrange
        doNothing().when(stopService).deleteStop(anyLong());

        // Act
        ResponseEntity<Void> response = stopController.deleteStop(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(stopService, times(1)).deleteStop(1L);
    }

    /**
     * Tests stop activation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testActivateStop_Success() {
        // Arrange
        doNothing().when(stopService).activateStop(anyLong());

        // Act
        ResponseEntity<Void> response = stopController.activateStop(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stopService, times(1)).activateStop(1L);
    }

    /**
     * Tests stop deactivation via PATCH endpoint.
     * Verifies that HTTP 204 status is returned.
     */
    @Test
    void testDeactivateStop_Success() {
        // Arrange
        doNothing().when(stopService).deactivateStop(anyLong());

        // Act
        ResponseEntity<Void> response = stopController.deactivateStop(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stopService, times(1)).deactivateStop(1L);
    }
}

