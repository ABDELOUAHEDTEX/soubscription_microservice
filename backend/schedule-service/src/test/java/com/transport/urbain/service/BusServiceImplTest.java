package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.BusMapper;
import com.transport.urbain.dto.request.CreateBusRequest;
import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.exception.BusNotFoundException;
import com.transport.urbain.exception.DuplicateBusException;
import com.transport.urbain.model.Bus;
import com.transport.urbain.model.BusStatus;
import com.transport.urbain.repository.BusRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BusServiceImpl.
 * <p>
 * This test class covers all business logic in BusServiceImpl including:
 * <ul>
 *     <li>Bus creation with validation</li>
 *     <li>Bus retrieval by ID and number</li>
 *     <li>Bus listing with pagination</li>
 *     <li>Bus update operations</li>
 *     <li>Bus deletion</li>
 *     <li>Bus status management</li>
 *     <li>Exception handling</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    @InjectMocks
    private BusServiceImpl busService;

    private Bus testBus;
    private CreateBusRequest createBusRequest;
    private BusResponse busResponse;

    /**
     * Sets up test data before each test method.
     * Creates mock objects for Bus, CreateBusRequest, and BusResponse.
     */
    @BeforeEach
    void setUp() {
        testBus = Bus.builder()
                .id(1L)
                .busNumber("BUS-001")
                .licensePlate("ABC-123")
                .model("Mercedes Citaro")
                .manufacturer("Mercedes")
                .year(2020)
                .capacity(50)
                .seatingCapacity(40)
                .standingCapacity(10)
                .status(BusStatus.ACTIVE)
                .hasWifi(true)
                .hasAirConditioning(true)
                .isAccessible(true)
                .hasGPS(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createBusRequest = new CreateBusRequest();
        createBusRequest.setBusNumber("BUS-001");
        createBusRequest.setLicensePlate("ABC-123");
        createBusRequest.setModel("Mercedes Citaro");
        createBusRequest.setManufacturer("Mercedes");
        createBusRequest.setYear(2020);
        createBusRequest.setCapacity(50);
        createBusRequest.setSeatingCapacity(40);
        createBusRequest.setStandingCapacity(10);
        createBusRequest.setStatus(BusStatus.ACTIVE);
        createBusRequest.setHasWifi(true);
        createBusRequest.setHasAirConditioning(true);
        createBusRequest.setIsAccessible(true);
        createBusRequest.setHasGPS(true);

        busResponse = new BusResponse();
        busResponse.setId(1L);
        busResponse.setBusNumber("BUS-001");
        busResponse.setLicensePlate("ABC-123");
        busResponse.setStatus(BusStatus.ACTIVE);
    }

    /**
     * Tests successful bus creation.
     * Verifies that a new bus is created when valid data is provided.
     */
    @Test
    void testCreateBus_Success() {
        // Arrange
        when(busRepository.existsByBusNumber(anyString())).thenReturn(false);
        when(busRepository.existsByLicensePlate(anyString())).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        BusResponse result = busService.createBus(createBusRequest);

        // Assert
        assertNotNull(result);
        assertEquals("BUS-001", result.getBusNumber());
        verify(busRepository, times(1)).existsByBusNumber("BUS-001");
        verify(busRepository, times(1)).existsByLicensePlate("ABC-123");
        verify(busRepository, times(1)).save(any(Bus.class));
    }

    /**
     * Tests bus creation failure when bus number already exists.
     * Verifies that DuplicateBusException is thrown.
     */
    @Test
    void testCreateBus_DuplicateBusNumber() {
        // Arrange
        when(busRepository.existsByBusNumber("BUS-001")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateBusException.class, () -> busService.createBus(createBusRequest));
        verify(busRepository, times(1)).existsByBusNumber("BUS-001");
        verify(busRepository, never()).save(any());
    }

    /**
     * Tests bus creation failure when license plate already exists.
     * Verifies that DuplicateBusException is thrown.
     */
    @Test
    void testCreateBus_DuplicateLicensePlate() {
        // Arrange
        when(busRepository.existsByBusNumber(anyString())).thenReturn(false);
        when(busRepository.existsByLicensePlate("ABC-123")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateBusException.class, () -> busService.createBus(createBusRequest));
        verify(busRepository, times(1)).existsByLicensePlate("ABC-123");
        verify(busRepository, never()).save(any());
    }

    /**
     * Tests successful bus retrieval by ID.
     * Verifies that a bus is retrieved when a valid ID is provided.
     */
    @Test
    void testGetBusById_Success() {
        // Arrange
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        BusResponse result = busService.getBusById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(busRepository, times(1)).findById(1L);
    }

    /**
     * Tests bus retrieval failure when bus ID doesn't exist.
     * Verifies that BusNotFoundException is thrown.
     */
    @Test
    void testGetBusById_NotFound() {
        // Arrange
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusNotFoundException.class, () -> busService.getBusById(999L));
        verify(busRepository, times(1)).findById(999L);
    }

    /**
     * Tests successful bus retrieval by bus number.
     * Verifies that a bus is retrieved when a valid bus number is provided.
     */
    @Test
    void testGetBusByNumber_Success() {
        // Arrange
        when(busRepository.findByBusNumber("BUS-001")).thenReturn(Optional.of(testBus));
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        BusResponse result = busService.getBusByNumber("BUS-001");

        // Assert
        assertNotNull(result);
        verify(busRepository, times(1)).findByBusNumber("BUS-001");
    }

    /**
     * Tests bus retrieval failure when bus number doesn't exist.
     * Verifies that BusNotFoundException is thrown.
     */
    @Test
    void testGetBusByNumber_NotFound() {
        // Arrange
        when(busRepository.findByBusNumber("BUS-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusNotFoundException.class, () -> busService.getBusByNumber("BUS-999"));
        verify(busRepository, times(1)).findByBusNumber("BUS-999");
    }

    /**
     * Tests retrieval of all buses with pagination.
     * Verifies that a page of buses is returned correctly.
     */
    @Test
    void testGetAllBuses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bus> busPage = new PageImpl<>(List.of(testBus));
        when(busRepository.findAll(pageable)).thenReturn(busPage);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        Page<BusResponse> result = busService.getAllBuses(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(busRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests retrieval of available buses (active and in service).
     * Verifies that active buses are returned correctly.
     */
    @Test
    void testGetAvailableBuses_Success() {
        // Arrange
        Page<Bus> busPage = new PageImpl<>(List.of(testBus));
        when(busRepository.findAllActiveBuses(any(Pageable.class))).thenReturn(busPage);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        List<BusResponse> result = busService.getAvailableBuses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Tests successful bus update operation.
     * Verifies that bus information is updated correctly.
     */
    @Test
    void testUpdateBus_Success() {
        // Arrange
        CreateBusRequest updateRequest = new CreateBusRequest();
        updateRequest.setLicensePlate("XYZ-789");
        updateRequest.setModel("Updated Model");
        updateRequest.setManufacturer("Updated Manufacturer");
        updateRequest.setYear(2021);
        updateRequest.setCapacity(60);

        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        BusResponse result = busService.updateBus(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, times(1)).save(any(Bus.class));
    }

    /**
     * Tests bus update failure when bus ID doesn't exist.
     * Verifies that BusNotFoundException is thrown.
     */
    @Test
    void testUpdateBus_NotFound() {
        // Arrange
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusNotFoundException.class, () -> busService.updateBus(999L, createBusRequest));
        verify(busRepository, times(1)).findById(999L);
        verify(busRepository, never()).save(any());
    }

    /**
     * Tests successful bus deletion.
     * Verifies that a bus is deleted when a valid ID is provided.
     */
    @Test
    void testDeleteBus_Success() {
        // Arrange
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        doNothing().when(busRepository).delete(any(Bus.class));

        // Act
        assertDoesNotThrow(() -> busService.deleteBus(1L));

        // Assert
        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, times(1)).delete(testBus);
    }

    /**
     * Tests bus deletion failure when bus ID doesn't exist.
     * Verifies that BusNotFoundException is thrown.
     */
    @Test
    void testDeleteBus_NotFound() {
        // Arrange
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusNotFoundException.class, () -> busService.deleteBus(999L));
        verify(busRepository, times(1)).findById(999L);
        verify(busRepository, never()).delete(any());
    }

    /**
     * Tests successful bus status update.
     * Verifies that bus status is updated correctly.
     */
    @Test
    void testUpdateBusStatus_Success() {
        // Arrange
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        // Act
        assertDoesNotThrow(() -> busService.updateBusStatus(1L, BusStatus.MAINTENANCE));

        // Assert
        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, times(1)).save(any(Bus.class));
    }

    /**
     * Tests retrieval of buses filtered by status.
     * Verifies that buses with the specified status are returned.
     */
    @Test
    void testGetBusesByStatus_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bus> busPage = new PageImpl<>(List.of(testBus));
        when(busRepository.findByStatus(BusStatus.ACTIVE, pageable)).thenReturn(busPage);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        Page<BusResponse> result = busService.getBusesByStatus(BusStatus.ACTIVE, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(busRepository, times(1)).findByStatus(BusStatus.ACTIVE, pageable);
    }

    /**
     * Tests bus search functionality.
     * Verifies that buses matching the search keyword are returned.
     */
    @Test
    void testSearchBuses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bus> busPage = new PageImpl<>(List.of(testBus));
        when(busRepository.searchBuses("BUS", pageable)).thenReturn(busPage);
        when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

        // Act
        Page<BusResponse> result = busService.searchBuses("BUS", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(busRepository, times(1)).searchBuses("BUS", pageable);
    }
}

