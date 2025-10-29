package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.RouteMapper;
import com.transport.urbain.dto.request.AddRouteStopRequest;
import com.transport.urbain.dto.request.CreateRouteRequest;
import com.transport.urbain.dto.response.RouteDetailsResponse;
import com.transport.urbain.dto.response.RouteResponse;
import com.transport.urbain.event.RouteChangedEvent;
import com.transport.urbain.event.producer.ScheduleEventProducer;
import com.transport.urbain.exception.DuplicateRouteException;
import com.transport.urbain.exception.RouteNotFoundException;
import com.transport.urbain.exception.StopNotFoundException;
import com.transport.urbain.model.Route;
import com.transport.urbain.model.RouteStop;
import com.transport.urbain.model.Stop;
import com.transport.urbain.repository.RouteRepository;
import com.transport.urbain.repository.RouteStopRepository;
import com.transport.urbain.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Implementation of RouteService interface.
 * <p>
 * Provides business logic for route management operations including CRUD operations,
 * caching, event publishing, and route-stop management. Integrates with repositories,
 * mappers, and event producers for complete route lifecycle management.
 * <p>
 * Features:
 * <ul>
 *     <li>Caching: Uses Redis for performance optimization</li>
 *     <li>Event Publishing: Publishes route change events to Kafka for real-time updates</li>
 *     <li>Route-Stop Management: Supports adding/removing stops with sequence ordering</li>
 *     <li>Detailed Routes: Provides comprehensive route details with stop information</li>
 *     <li>Transactional: All write operations are transactional</li>
 *     <li>Logging: Comprehensive logging for debugging and monitoring</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;
    private final RouteMapper routeMapper;
    private final ScheduleEventProducer scheduleEventProducer;

    @Override
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public RouteResponse createRoute(CreateRouteRequest request) {
        log.info("Creating new route: {}", request.getRouteNumber());

        if (routeRepository.existsByRouteNumber(request.getRouteNumber())) {
            throw new DuplicateRouteException("Route number already exists: " + request.getRouteNumber());
        }

        Route route = Route.builder()
                .routeNumber(request.getRouteNumber())
                .routeName(request.getRouteName())
                .description(request.getDescription())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .distance(request.getDistance())
                .estimatedDuration(request.getEstimatedDuration())
                .isActive(true)
                .isCircular(request.getIsCircular() != null ? request.getIsCircular() : false)
                .color(request.getColor())
                .build();

        route = routeRepository.save(route);

        log.info("Route created successfully: {}", route.getRouteNumber());

        return routeMapper.toRouteResponse(route);
    }

    @Override
    @Cacheable(value = "routes", key = "#id")
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        return routeMapper.toRouteResponse(route);
    }

    @Override
    @Cacheable(value = "routes", key = "#routeNumber")
    public RouteResponse getRouteByNumber(String routeNumber) {
        Route route = routeRepository.findByRouteNumber(routeNumber)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with number: " + routeNumber));
        return routeMapper.toRouteResponse(route);
    }

    @Override
    @Cacheable(value = "routeDetails", key = "#id")
    public RouteDetailsResponse getRouteDetails(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        var stops = routeStopRepository.findByRouteIdOrderBySequenceNumberAsc(id)
                .stream()
                .map(rs -> RouteDetailsResponse.RouteStopDetail.builder()
                        .stopId(rs.getStop().getId())
                        .stopCode(rs.getStop().getStopCode())
                        .stopName(rs.getStop().getStopName())
                        .latitude(rs.getStop().getLatitude())
                        .longitude(rs.getStop().getLongitude())
                        .sequenceNumber(rs.getSequenceNumber())
                        .distanceFromOrigin(rs.getDistanceFromOrigin())
                        .timeFromOrigin(rs.getTimeFromOrigin())
                        .dwellTime(rs.getDwellTime())
                        .build())
                .collect(Collectors.toList());

        return RouteDetailsResponse.builder()
                .id(route.getId())
                .routeNumber(route.getRouteNumber())
                .routeName(route.getRouteName())
                .description(route.getDescription())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .distance(route.getDistance())
                .estimatedDuration(route.getEstimatedDuration())
                .isActive(route.getIsActive())
                .isCircular(route.getIsCircular())
                .color(route.getColor())
                .stops(stops)
                .build();
    }

    @Override
    public Page<RouteResponse> getAllRoutes(Pageable pageable) {
        return routeRepository.findAll(pageable)
                .map(routeMapper::toRouteResponse);
    }

    @Override
    @Cacheable(value = "activeRoutes")
    public Page<RouteResponse> getActiveRoutes(Pageable pageable) {
        return routeRepository.findAllActiveRoutes(pageable)
                .map(routeMapper::toRouteResponse);
    }

    @Override
    public Page<RouteResponse> searchRoutes(String keyword, Pageable pageable) {
        return routeRepository.searchRoutes(keyword, pageable)
                .map(routeMapper::toRouteResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"routes", "routeDetails"}, key = "#id")
    public RouteResponse updateRoute(Long id, CreateRouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        route.setRouteName(request.getRouteName());
        route.setDescription(request.getDescription());
        route.setOrigin(request.getOrigin());
        route.setDestination(request.getDestination());
        route.setDistance(request.getDistance());
        route.setEstimatedDuration(request.getEstimatedDuration());
        if (request.getIsCircular() != null) {
            route.setIsCircular(request.getIsCircular());
        }
        if (request.getColor() != null) {
            route.setColor(request.getColor());
        }

        route = routeRepository.save(route);

        // Publish route changed event
        scheduleEventProducer.publishRouteChanged(new RouteChangedEvent(
                route.getId(),
                route.getRouteNumber(),
                route.getRouteName(),
                LocalDateTime.now()
        ));

        log.info("Route updated successfully: {}", route.getRouteNumber());

        return routeMapper.toRouteResponse(route);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"routes", "routeDetails", "activeRoutes"}, allEntries = true)
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        routeRepository.delete(route);

        log.info("Route deleted successfully: {}", route.getRouteNumber());
    }

    @Override
    @Transactional
    @CacheEvict(value = "routeDetails", key = "#routeId")
    public void addStopToRoute(Long routeId, AddRouteStopRequest request) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));

        Stop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + request.getStopId()));

        RouteStop routeStop = RouteStop.builder()
                .route(route)
                .stop(stop)
                .sequenceNumber(request.getSequenceNumber())
                .distanceFromOrigin(request.getDistanceFromOrigin())
                .timeFromOrigin(request.getTimeFromOrigin())
                .dwellTime(request.getDwellTime() != null ? request.getDwellTime() : 1)
                .build();

        routeStopRepository.save(routeStop);

        log.info("Stop {} added to route {}", stop.getStopCode(), route.getRouteNumber());
    }

    @Override
    @Transactional
    @CacheEvict(value = "routeDetails", key = "#routeId")
    public void removeStopFromRoute(Long routeId, Long stopId) {
        routeStopRepository.deleteByRouteIdAndStopId(routeId, stopId);
        log.info("Stop {} removed from route {}", stopId, routeId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"routes", "activeRoutes"}, allEntries = true)
    public void activateRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        route.setIsActive(true);
        routeRepository.save(route);

        log.info("Route activated: {}", route.getRouteNumber());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"routes", "activeRoutes"}, allEntries = true)
    public void deactivateRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        route.setIsActive(false);
        routeRepository.save(route);

        log.info("Route deactivated: {}", route.getRouteNumber());
    }
}
