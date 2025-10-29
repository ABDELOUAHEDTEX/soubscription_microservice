package com.transport.urbain.service;

import com.transport.urbain.dto.request.AddRouteStopRequest;
import com.transport.urbain.dto.request.CreateRouteRequest;
import com.transport.urbain.dto.response.RouteDetailsResponse;
import com.transport.urbain.dto.response.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for route management operations.
 * <p>
 * Provides methods for CRUD operations on bus routes, including creation, retrieval,
 * updating, deletion, and activation/deactivation. Supports route-stop management,
 * detailed route information retrieval, and route search functionality.
 */
public interface RouteService {

    RouteResponse createRoute(CreateRouteRequest request);

    RouteResponse getRouteById(Long id);

    RouteResponse getRouteByNumber(String routeNumber);

    RouteDetailsResponse getRouteDetails(Long id);

    Page<RouteResponse> getAllRoutes(Pageable pageable);

    Page<RouteResponse> getActiveRoutes(Pageable pageable);

    Page<RouteResponse> searchRoutes(String keyword, Pageable pageable);

    RouteResponse updateRoute(Long id, CreateRouteRequest request);

    void deleteRoute(Long id);

    void addStopToRoute(Long routeId, AddRouteStopRequest request);

    void removeStopFromRoute(Long routeId, Long stopId);

    void activateRoute(Long id);

    void deactivateRoute(Long id);
}