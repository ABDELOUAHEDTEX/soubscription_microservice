package com.transport.urbain.service;

import com.transport.urbain.dto.request.CreateBusRequest;
import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.model.BusStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for bus management operations.
 * <p>
 * Provides methods for CRUD operations on buses, including creation, retrieval,
 * updating, deletion, and status management. Supports searching, filtering by status,
 * and accessing available buses for schedule assignment.
 */
public interface BusService {

    /**
     * Creates a new bus in the system.
     *
     * @param request the bus creation request containing bus details
     * @return the created bus response
     */
    BusResponse createBus(CreateBusRequest request);

    /**
     * Retrieves a bus by its unique identifier.
     *
     * @param id the bus ID
     * @return the bus response
     */
    BusResponse getBusById(Long id);

    /**
     * Retrieves a bus by its unique bus number.
     *
     * @param busNumber the bus number
     * @return the bus response
     */
    BusResponse getBusByNumber(String busNumber);

    /**
     * Retrieves all buses with pagination support.
     *
     * @param pageable pagination and sorting parameters
     * @return a page of bus responses
     */
    Page<BusResponse> getAllBuses(Pageable pageable);

    /**
     * Retrieves all available buses (active and in service).
     *
     * @return list of available bus responses
     */
    List<BusResponse> getAvailableBuses();

    /**
     * Retrieves all active buses with pagination support.
     *
     * @param pageable pagination and sorting parameters
     * @return a page of active bus responses
     */
    Page<BusResponse> getActiveBuses(Pageable pageable);

    /**
     * Retrieves buses filtered by status with pagination support.
     *
     * @param status the bus status to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of buses with the specified status
     */
    Page<BusResponse> getBusesByStatus(BusStatus status, Pageable pageable);

    /**
     * Searches buses by keyword (bus number, license plate, or model).
     *
     * @param keyword the search keyword
     * @param pageable pagination and sorting parameters
     * @return a page of matching bus responses
     */
    Page<BusResponse> searchBuses(String keyword, Pageable pageable);

    /**
     * Updates an existing bus's information.
     *
     * @param id the bus ID to update
     * @param request the update request containing modified bus details
     * @return the updated bus response
     */
    BusResponse updateBus(Long id, CreateBusRequest request);

    /**
     * Deletes a bus from the system.
     *
     * @param id the bus ID to delete
     */
    void deleteBus(Long id);

    /**
     * Updates the status of a specific bus.
     *
     * @param id the bus ID
     * @param status the new status to apply
     */
    void updateBusStatus(Long id, BusStatus status);
}
