package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.StopMapper;
import com.transport.urbain.dto.request.CreateStopRequest;
import com.transport.urbain.dto.response.StopResponse;
import com.transport.urbain.exception.DuplicateStopException;
import com.transport.urbain.exception.StopNotFoundException;
import com.transport.urbain.model.Stop;
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
 * Unit tests for StopServiceImpl.
 * <p>
 * This test class covers all business logic in StopServiceImpl including:
 * <ul>
 *     <li>Stop creation with validation</li>
 *     <li>Stop retrieval by ID and code</li>
 *     <li>Stop listing with pagination</li>
 *     <li>Stop update operations</li>
 *     <li>Stop deletion</li>
 *     <li>Stop activation/deactivation</li>
 *     <li>Geospatial queries</li>
 *     <li>Exception handling</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class StopServiceImplTest {

    @Mock
    private StopRepository stopRepository;

    @Mock
    private StopMapper stopMapper;

    @InjectMocks
    private StopServiceImpl stopService;

    private Stop testStop;
    private CreateStopRequest createStopRequest;
    private StopResponse stopResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for Stop, CreateStopRequest, and StopResponse.
     */
    @BeforeEach
    void setUp() {
        testStop = Stop.builder()
                .id(1L)
                .stopCode("ST001")
                .stopName("Central Station")
                .description("Main transportation hub")
                .address("123 Main Street")
                .latitude(new BigDecimal("12.34567890"))
                .longitude(new BigDecimal("98.76543210"))
                .city("Metro City")
                .district("Downtown")
                .postalCode("12345")
                .hasWaitingShelter(true)
                .hasSeating(true)
                .isAccessible(true)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createStopRequest = new CreateStopRequest();
        createStopRequest.setStopCode("ST001");
        createStopRequest.setStopName("Central Station");
        createStopRequest.setDescription("Main transportation hub");
        createStopRequest.setAddress("123 Main Street");
        createStopRequest.setLatitude(new BigDecimal("12.34567890"));
        createStopRequest.setLongitude(new BigDecimal("98.76543210"));
        createStopRequest.setCity("Metro City");
        createStopRequest.setDistrict("Downtown");
        createStopRequest.setPostalCode("12345");
        createStopRequest.setHasWaitingShelter(true);
        createStopRequest.setHasSeating(true);
        createStopRequest.setIsAccessible(true);

        stopResponse = new StopResponse();
        stopResponse.setId(1L);
        stopResponse.setStopCode("ST001");
        stopResponse.setStopName("Central Station");
        stopResponse.setIsActive(true);
    }

    /**
     * Tests successful stop creation.
     * Verifies that a new stop is created when valid data is provided.
     */
    @Test
    void testCreateStop_Success() {
        // Arrange
        when(stopRepository.existsByStopCode(anyString())).thenReturn(false);
        when(stopRepository.save(any(Stop.class))).thenReturn(testStop);
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        StopResponse result = stopService.createStop(createStopRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ST001", result.getStopCode());
        verify(stopRepository, times(1)).existsByStopCode("ST001");
        verify(stopRepository, times(1)).save(any(Stop.class));
    }

    /**
     * Tests stop creation failure when stop code already exists.
     * Verifies that DuplicateStopException is thrown.
     */
    @Test
    void testCreateStop_DuplicateStopCode() {
        // Arrange
        when(stopRepository.existsByStopCode("ST001")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateStopException.class, () -> stopService.createStop(createStopRequest));
        verify(stopRepository, times(1)).existsByStopCode("ST001");
        verify(stopRepository, never()).save(any());
    }

    /**
     * Tests successful stop retrieval by ID.
     * Verifies that a stop is retrieved when a valid ID is provided.
     */
    @Test
    void testGetStopById_Success() {
        // Arrange
        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        StopResponse result = stopService.getStopById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(stopRepository, times(1)).findById(1L);
    }

    /**
     * Tests stop retrieval failure when stop ID doesn't exist.
     * Verifies that StopNotFoundException is thrown.
     */
    @Test
    void testGetStopById_NotFound() {
        // Arrange
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StopNotFoundException.class, () -> stopService.getStopById(999L));
        verify(stopRepository, times(1)).findById(999L);
    }

    /**
     * Tests successful stop retrieval by stop code.
     * Verifies that a stop is retrieved when a valid stop code is provided.
     */
    @Test
    void testGetStopByCode_Success() {
        // Arrange
        when(stopRepository.findByStopCode("ST001")).thenReturn(Optional.of(testStop));
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        StopResponse result = stopService.getStopByCode("ST001");

        // Assert
        assertNotNull(result);
        verify(stopRepository, times(1)).findByStopCode("ST001");
    }

    /**
     * Tests stop retrieval failure when stop code doesn't exist.
     * Verifies that StopNotFoundException is thrown.
     */
    @Test
    void testGetStopByCode_NotFound() {
        // Arrange
        when(stopRepository.findByStopCode("ST999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StopNotFoundException.class, () -> stopService.getStopByCode("ST999"));
        verify(stopRepository, times(1)).findByStopCode("ST999");
    }

    /**
     * Tests retrieval of all stops with pagination.
     * Verifies that a page of stops is returned correctly.
     */
    @Test
    void testGetAllStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stop> stopPage = new PageImpl<>(List.of(testStop));
        when(stopRepository.findAll(pageable)).thenReturn(stopPage);
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        Page<StopResponse> result = stopService.getAllStops(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stopRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests successful stop update operation.
     * Verifies that stop information is updated correctly.
     */
    @Test
    void testUpdateStop_Success() {
        // Arrange
        CreateStopRequest updateRequest = new CreateStopRequest();
        updateRequest.setStopName("Updated Central Station");
        updateRequest.setAddress("456 Updated Street");

        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        when(stopRepository.save(any(Stop.class))).thenReturn(testStop);
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        StopResponse result = stopService.updateStop(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(stopRepository, times(1)).findById(1L);
        verify(stopRepository, times(1)).save(any(Stop.class));
    }

    /**
     * Tests stop update failure when stop ID doesn't exist.
     * Verifies that StopNotFoundException is thrown.
     */
    @Test
    void testUpdateStop_NotFound() {
        // Arrange
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StopNotFoundException.class, () -> stopService.updateStop(999L, createStopRequest));
        verify(stopRepository, times(1)).findById(999L);
        verify(stopRepository, never()).save(any());
    }

    /**
     * Tests successful stop deletion.
     * Verifies that a stop is deleted when a valid ID is provided.
     */
    @Test
    void testDeleteStop_Success() {
        // Arrange
        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        doNothing().when(stopRepository).delete(any(Stop.class));

        // Act
        assertDoesNotThrow(() -> stopService.deleteStop(1L));

        // Assert
        verify(stopRepository, times(1)).findById(1L);
        verify(stopRepository, times(1)).delete(testStop);
    }

    /**
     * Tests stop deletion failure when stop ID doesn't exist.
     * Verifies that StopNotFoundException is thrown.
     */
    @Test
    void testDeleteStop_NotFound() {
        // Arrange
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StopNotFoundException.class, () -> stopService.deleteStop(999L));
        verify(stopRepository, times(1)).findById(999L);
        verify(stopRepository, never()).delete(any());
    }

    /**
     * Tests successful stop activation.
     * Verifies that a stop is activated correctly.
     */
    @Test
    void testActivateStop_Success() {
        // Arrange
        testStop.setIsActive(false);
        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        when(stopRepository.save(any(Stop.class))).thenReturn(testStop);

        // Act
        assertDoesNotThrow(() -> stopService.activateStop(1L));

        // Assert
        verify(stopRepository, times(1)).findById(1L);
        verify(stopRepository, times(1)).save(any(Stop.class));
    }

    /**
     * Tests successful stop deactivation.
     * Verifies that a stop is deactivated correctly.
     */
    @Test
    void testDeactivateStop_Success() {
        // Arrange
        when(stopRepository.findById(1L)).thenReturn(Optional.of(testStop));
        when(stopRepository.save(any(Stop.class))).thenReturn(testStop);

        // Act
        assertDoesNotThrow(() -> stopService.deactivateStop(1L));

        // Assert
        verify(stopRepository, times(1)).findById(1L);
        verify(stopRepository, times(1)).save(any(Stop.class));
    }

    /**
     * Tests retrieval of nearby stops using geospatial calculation.
     * Verifies that stops within a specified radius are returned.
     */
    @Test
    void testGetNearbyStops_Success() {
        // Arrange
        BigDecimal latitude = new BigDecimal("12.34567890");
        BigDecimal longitude = new BigDecimal("98.76543210");
        Double radius = 5.0;

        when(stopRepository.findNearbyStops(latitude, longitude, radius))
                .thenReturn(List.of(testStop));
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        List<StopResponse> result = stopService.getNearbyStops(latitude, longitude, radius);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stopRepository, times(1)).findNearbyStops(latitude, longitude, radius);
    }

    /**
     * Tests stop search functionality.
     * Verifies that stops matching the search keyword are returned.
     */
    @Test
    void testSearchStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stop> stopPage = new PageImpl<>(List.of(testStop));
        when(stopRepository.searchStops("Central", pageable)).thenReturn(stopPage);
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        Page<StopResponse> result = stopService.searchStops("Central", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stopRepository, times(1)).searchStops("Central", pageable);
    }

    /**
     * Tests retrieval of active stops with pagination.
     * Verifies that a page of active stops is returned correctly.
     */
    @Test
    void testGetActiveStops_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stop> stopPage = new PageImpl<>(List.of(testStop));
        when(stopRepository.findAllActiveStops(pageable)).thenReturn(stopPage);
        when(stopMapper.toStopResponse(any(Stop.class))).thenReturn(stopResponse);

        // Act
        Page<StopResponse> result = stopService.getActiveStops(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stopRepository, times(1)).findAllActiveStops(pageable);
    }
}

