package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.StopMapper;
import com.transport.urbain.dto.request.CreateStopRequest;
import com.transport.urbain.dto.response.StopResponse;
import com.transport.urbain.exception.DuplicateStopException;
import com.transport.urbain.exception.StopNotFoundException;
import com.transport.urbain.model.Stop;
import com.transport.urbain.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StopService interface.
 * <p>
 * Provides business logic for stop management operations including CRUD operations,
 * caching, and geospatial queries. Integrates with repositories and mappers for
 * complete stop lifecycle management.
 * <p>
 * Features:
 * <ul>
 *     <li>Caching: Uses Redis for performance optimization</li>
 *     <li>Validation: Prevents duplicate stop codes</li>
 *     <li>Geospatial Queries: Supports nearby stop searches</li>
 *     <li>Transactional: All write operations are transactional</li>
 *     <li>Logging: Comprehensive logging for debugging and monitoring</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StopServiceImpl implements StopService {

    private final StopRepository stopRepository;
    private final StopMapper stopMapper;

    @Override
    @Transactional
    @CacheEvict(value = "stops", allEntries = true)
    public StopResponse createStop(CreateStopRequest request) {
        log.info("Creating new stop: {}", request.getStopCode());

        if (stopRepository.existsByStopCode(request.getStopCode())) {
            throw new DuplicateStopException("Stop code already exists: " + request.getStopCode());
        }

        Stop stop = Stop.builder()
                .stopCode(request.getStopCode())
                .stopName(request.getStopName())
                .description(request.getDescription())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .city(request.getCity())
                .district(request.getDistrict())
                .postalCode(request.getPostalCode())
                .hasWaitingShelter(request.getHasWaitingShelter() != null ? request.getHasWaitingShelter() : false)
                .hasSeating(request.getHasSeating() != null ? request.getHasSeating() : false)
                .isAccessible(request.getIsAccessible() != null ? request.getIsAccessible() : false)
                .isActive(true)
                .build();

        stop = stopRepository.save(stop);
        log.info("Stop created successfully: {}", stop.getStopCode());
        return stopMapper.toStopResponse(stop);
    }

    @Override
    @Cacheable(value = "stops", key = "#id")
    public StopResponse getStopById(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        return stopMapper.toStopResponse(stop);
    }

    @Override
    @Cacheable(value = "stops", key = "#stopCode")
    public StopResponse getStopByCode(String stopCode) {
        Stop stop = stopRepository.findByStopCode(stopCode)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with code: " + stopCode));
        return stopMapper.toStopResponse(stop);
    }

    @Override
    public Page<StopResponse> getAllStops(Pageable pageable) {
        return stopRepository.findAll(pageable).map(stopMapper::toStopResponse);
    }

    @Override
    @Cacheable(value = "activeStops")
    public Page<StopResponse> getActiveStops(Pageable pageable) {
        return stopRepository.findAllActiveStops(pageable).map(stopMapper::toStopResponse);
    }

    @Override
    public Page<StopResponse> searchStops(String keyword, Pageable pageable) {
        return stopRepository.searchStops(keyword, pageable).map(stopMapper::toStopResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "stops", key = "#id")
    public StopResponse updateStop(Long id, CreateStopRequest request) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));

        stop.setStopName(request.getStopName());
        stop.setDescription(request.getDescription());
        stop.setAddress(request.getAddress());
        stop.setLatitude(request.getLatitude());
        stop.setLongitude(request.getLongitude());
        stop.setCity(request.getCity());
        stop.setDistrict(request.getDistrict());
        stop.setPostalCode(request.getPostalCode());
        if (request.getHasWaitingShelter() != null) stop.setHasWaitingShelter(request.getHasWaitingShelter());
        if (request.getHasSeating() != null) stop.setHasSeating(request.getHasSeating());
        if (request.getIsAccessible() != null) stop.setIsAccessible(request.getIsAccessible());

        stop = stopRepository.save(stop);
        log.info("Stop updated successfully: {}", stop.getStopCode());
        return stopMapper.toStopResponse(stop);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stops", "activeStops"}, allEntries = true)
    public void deleteStop(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        stopRepository.delete(stop);
        log.info("Stop deleted successfully: {}", stop.getStopCode());
    }

    @Override
    public List<StopResponse> getNearbyStops(BigDecimal latitude, BigDecimal longitude, Double radius) {
        return stopRepository.findNearbyStops(latitude, longitude, radius)
                .stream()
                .map(stopMapper::toStopResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stops", "activeStops"}, allEntries = true)
    public void activateStop(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        stop.setIsActive(true);
        stopRepository.save(stop);
        log.info("Stop activated: {}", stop.getStopCode());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stops", "activeStops"}, allEntries = true)
    public void deactivateStop(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        stop.setIsActive(false);
        stopRepository.save(stop);
        log.info("Stop deactivated: {}", stop.getStopCode());
    }
}
