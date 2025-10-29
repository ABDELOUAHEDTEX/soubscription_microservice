package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.BusMapper;
import com.transport.urbain.dto.request.CreateBusRequest;
import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.exception.BusNotFoundException;
import com.transport.urbain.exception.DuplicateBusException;
import com.transport.urbain.model.Bus;
import com.transport.urbain.model.BusStatus;
import com.transport.urbain.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BusService interface.
 * <p>
 * Provides business logic for bus management operations including CRUD operations,
 * caching, and duplicate detection. Integrates with repositories, mappers, and
 * event producers for complete bus lifecycle management.
 * <p>
 * Features:
 * <ul>
 *     <li>Caching: Uses Redis for performance optimization</li>
 *     <li>Validation: Prevents duplicate bus numbers and license plates</li>
 *     <li>Transactional: All write operations are transactional</li>
 *     <li>Logging: Comprehensive logging for debugging and monitoring</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    @Override
    @Transactional
    @CacheEvict(value = "buses", allEntries = true)
    public BusResponse createBus(CreateBusRequest request) {
        log.info("Creating new bus: {}", request.getBusNumber());

        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new DuplicateBusException("Bus number already exists: " + request.getBusNumber());
        }

        if (busRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateBusException("License plate already exists: " + request.getLicensePlate());
        }

        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .licensePlate(request.getLicensePlate())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .year(request.getYear())
                .capacity(request.getCapacity())
                .seatingCapacity(request.getSeatingCapacity())
                .standingCapacity(request.getStandingCapacity())
                .status(request.getStatus() != null ? request.getStatus() : BusStatus.ACTIVE)
                .hasAirConditioning(request.getHasAirConditioning() != null ? request.getHasAirConditioning() : false)
                .hasWifi(request.getHasWifi() != null ? request.getHasWifi() : false)
                .isAccessible(request.getIsAccessible() != null ? request.getIsAccessible() : false)
                .hasGPS(request.getHasGPS() != null ? request.getHasGPS() : true)
                .notes(request.getNotes())
                .build();

        bus = busRepository.save(bus);
        log.info("Bus created successfully: {}", bus.getBusNumber());
        return busMapper.toBusResponse(bus);
    }

    @Override
    @Cacheable(value = "buses", key = "#id")
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + id));
        return busMapper.toBusResponse(bus);
    }

    @Override
    @Cacheable(value = "buses", key = "#busNumber")
    public BusResponse getBusByNumber(String busNumber) {
        Bus bus = busRepository.findByBusNumber(busNumber)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with number: " + busNumber));
        return busMapper.toBusResponse(bus);
    }

    @Override
    public Page<BusResponse> getAllBuses(Pageable pageable) {
        return busRepository.findAll(pageable).map(busMapper::toBusResponse);
    }

    @Override
    @Cacheable(value = "availableBuses")
    public List<BusResponse> getAvailableBuses() {
        return busRepository.findAllActiveBuses(Pageable.unpaged())
                .getContent()
                .stream()
                .map(busMapper::toBusResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BusResponse> getActiveBuses(Pageable pageable) {
        return busRepository.findAllActiveBuses(pageable).map(busMapper::toBusResponse);
    }

    @Override
    public Page<BusResponse> getBusesByStatus(BusStatus status, Pageable pageable) {
        return busRepository.findByStatus(status, pageable).map(busMapper::toBusResponse);
    }

    @Override
    public Page<BusResponse> searchBuses(String keyword, Pageable pageable) {
        return busRepository.searchBuses(keyword, pageable).map(busMapper::toBusResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "buses", key = "#id")
    public BusResponse updateBus(Long id, CreateBusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + id));

        bus.setLicensePlate(request.getLicensePlate());
        bus.setManufacturer(request.getManufacturer());
        bus.setModel(request.getModel());
        bus.setYear(request.getYear());
        bus.setCapacity(request.getCapacity());
        if (request.getSeatingCapacity() != null) bus.setSeatingCapacity(request.getSeatingCapacity());
        if (request.getStandingCapacity() != null) bus.setStandingCapacity(request.getStandingCapacity());
        if (request.getStatus() != null) bus.setStatus(request.getStatus());
        if (request.getHasAirConditioning() != null) bus.setHasAirConditioning(request.getHasAirConditioning());
        if (request.getHasWifi() != null) bus.setHasWifi(request.getHasWifi());
        if (request.getIsAccessible() != null) bus.setIsAccessible(request.getIsAccessible());
        if (request.getHasGPS() != null) bus.setHasGPS(request.getHasGPS());
        if (request.getNotes() != null) bus.setNotes(request.getNotes());

        bus = busRepository.save(bus);
        log.info("Bus updated successfully: {}", bus.getBusNumber());
        return busMapper.toBusResponse(bus);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"buses", "availableBuses"}, allEntries = true)
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + id));
        busRepository.delete(bus);
        log.info("Bus deleted successfully: {}", bus.getBusNumber());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"buses", "availableBuses"}, allEntries = true)
    public void updateBusStatus(Long id, BusStatus status) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + id));
        bus.setStatus(status);
        busRepository.save(bus);
        log.info("Bus status updated: {} -> {}", bus.getBusNumber(), status);
    }
}