package com.transport.urbain.service;

import com.transport.urbain.dto.request.CreateStopRequest;
import com.transport.urbain.dto.response.StopResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for stop management operations.
 * <p>
 * Provides methods for CRUD operations on bus stops, including creation, retrieval,
 * updating, deletion, and activation/deactivation. Supports location-based queries,
 * geospatial searches, and accessibility filtering.
 */
public interface StopService {

    StopResponse createStop(CreateStopRequest request);

    StopResponse getStopById(Long id);

    StopResponse getStopByCode(String stopCode);

    Page<StopResponse> getAllStops(Pageable pageable);

    Page<StopResponse> getActiveStops(Pageable pageable);

    Page<StopResponse> searchStops(String keyword, Pageable pageable);

    StopResponse updateStop(Long id, CreateStopRequest request);

    void deleteStop(Long id);

    List<StopResponse> getNearbyStops(BigDecimal latitude, BigDecimal longitude, Double radius);

    void activateStop(Long id);

    void deactivateStop(Long id);
}
