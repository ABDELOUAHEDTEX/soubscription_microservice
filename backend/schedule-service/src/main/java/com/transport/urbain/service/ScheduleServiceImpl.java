package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.ScheduleMapper;
import com.transport.urbain.dto.request.CreateScheduleRequest;
import com.transport.urbain.dto.request.SearchScheduleRequest;
import com.transport.urbain.dto.request.UpdateScheduleRequest;
import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.event.ScheduleCreatedEvent;
import com.transport.urbain.event.ScheduleUpdatedEvent;
import com.transport.urbain.event.producer.ScheduleEventProducer;
import com.transport.urbain.exception.InvalidScheduleException;
import com.transport.urbain.exception.RouteNotFoundException;
import com.transport.urbain.exception.ScheduleNotFoundException;
import com.transport.urbain.model.*;
import com.transport.urbain.repository.BusRepository;
import com.transport.urbain.repository.RouteRepository;
import com.transport.urbain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of ScheduleService interface.
 * <p>
 * Provides business logic for schedule management operations including CRUD operations,
 * caching, event publishing, and conflict detection. Integrates with repositories,
 * mappers, and event producers for complete schedule lifecycle management.
 * <p>
 * Features:
 * <ul>
 *     <li>Caching: Uses Redis for performance optimization</li>
 *     <li>Event Publishing: Publishes schedule events to Kafka for real-time updates</li>
 *     <li>Validation: Validates schedule timing constraints</li>
 *     <li>Time-based Queries: Supports today's schedules and upcoming schedules</li>
 *     <li>Transactional: All write operations are transactional</li>
 *     <li>Logging: Comprehensive logging for debugging and monitoring</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleEventProducer scheduleEventProducer;

    @Override
    @Transactional
    @CacheEvict(value = "schedules", allEntries = true)
    public ScheduleResponse createSchedule(CreateScheduleRequest request) {
        log.info("Creating new schedule for route: {}", request.getRouteId());

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + request.getRouteId()));

        Bus bus = null;
        if (request.getBusId() != null) {
            bus = busRepository.findById(request.getBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found with id: " + request.getBusId()));
        }

        // Validate schedule time
        if (request.getDepartureTime().isAfter(request.getArrivalTime())) {
            throw new InvalidScheduleException("Departure time must be before arrival time");
        }

        Schedule schedule = Schedule.builder()
                .route(route)
                .bus(bus)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .scheduleType(request.getScheduleType())
                .daysOfWeek(request.getDaysOfWeek() != null ? request.getDaysOfWeek() : new HashSet<>())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .frequency(request.getFrequency())
                .isActive(true)
                .notes(request.getNotes())
                .build();

        schedule = scheduleRepository.save(schedule);

        // Publish schedule created event
        scheduleEventProducer.publishScheduleCreated(new ScheduleCreatedEvent(
                schedule.getId(),
                route.getId(),
                route.getRouteNumber(),
                schedule.getDepartureTime(),
                LocalDateTime.now()
        ));

        log.info("Schedule created successfully for route: {}", route.getRouteNumber());
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Override
    @Cacheable(value = "schedules", key = "#id")
    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Override
    public Page<ScheduleResponse> getAllSchedules(Pageable pageable) {
        return scheduleRepository.findAll(pageable).map(scheduleMapper::toScheduleResponse);
    }

    @Override
    @Cacheable(value = "routeSchedules", key = "#routeId")
    public List<ScheduleResponse> getSchedulesByRoute(Long routeId) {
        return scheduleRepository.findByRouteId(routeId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(scheduleMapper::toScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "activeSchedules")
    public Page<ScheduleResponse> getActiveSchedules(Pageable pageable) {
        return scheduleRepository.findByIsActive(true, pageable).map(scheduleMapper::toScheduleResponse);
    }

    @Override
    public List<ScheduleResponse> searchSchedules(SearchScheduleRequest request) {
        List<Schedule> schedules;

        if (request.getRouteId() != null) {
            schedules = scheduleRepository.findByRouteId(request.getRouteId(), Pageable.unpaged()).getContent();
        } else if (request.getDate() != null && request.getFromTime() != null) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.getDate().getDayOfWeek().name());
            schedules = scheduleRepository.findActiveSchedulesForRouteAndDate(
                    request.getRouteId() != null ? request.getRouteId() : 0L,
                    request.getDate(),
                    dayOfWeek
            );
        } else {
            schedules = scheduleRepository.findAll();
        }

        return schedules.stream()
                .map(scheduleMapper::toScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"schedules", "routeSchedules"}, allEntries = true)
    public ScheduleResponse updateSchedule(Long id, UpdateScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));

        if (request.getDepartureTime() != null) {
            schedule.setDepartureTime(request.getDepartureTime());
        }
        if (request.getArrivalTime() != null) {
            schedule.setArrivalTime(request.getArrivalTime());
        }
        if (request.getScheduleType() != null) {
            schedule.setScheduleType(request.getScheduleType());
        }
        if (request.getDaysOfWeek() != null) {
            schedule.setDaysOfWeek(request.getDaysOfWeek());
        }
        if (request.getValidFrom() != null) {
            schedule.setValidFrom(request.getValidFrom());
        }
        if (request.getValidUntil() != null) {
            schedule.setValidUntil(request.getValidUntil());
        }
        if (request.getFrequency() != null) {
            schedule.setFrequency(request.getFrequency());
        }
        if (request.getIsActive() != null) {
            schedule.setIsActive(request.getIsActive());
        }
        if (request.getNotes() != null) {
            schedule.setNotes(request.getNotes());
        }

        schedule = scheduleRepository.save(schedule);

        // Publish schedule updated event
        scheduleEventProducer.publishScheduleUpdated(new ScheduleUpdatedEvent(
                schedule.getId(),
                schedule.getRoute().getId(),
                schedule.getRoute().getRouteNumber(),
                LocalDateTime.now()
        ));

        log.info("Schedule updated successfully: {}", schedule.getId());
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"schedules", "routeSchedules", "activeSchedules"}, allEntries = true)
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        scheduleRepository.delete(schedule);
        log.info("Schedule deleted successfully: {}", schedule.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"schedules", "activeSchedules"}, allEntries = true)
    public void activateSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        schedule.setIsActive(true);
        scheduleRepository.save(schedule);
        log.info("Schedule activated: {}", schedule.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"schedules", "activeSchedules"}, allEntries = true)
    public void deactivateSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        schedule.setIsActive(false);
        scheduleRepository.save(schedule);
        log.info("Schedule deactivated: {}", schedule.getId());
    }

    @Override
    public List<ScheduleResponse> getTodaySchedules(Long routeId) {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(today.getDayOfWeek().name());
        return scheduleRepository.findActiveSchedulesForRouteAndDate(routeId, today, dayOfWeek)
                .stream()
                .map(scheduleMapper::toScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getUpcomingSchedules(Long routeId, LocalTime fromTime) {
        List<Schedule> allSchedules = scheduleRepository.findByRouteId(routeId, Pageable.unpaged()).getContent();
        return allSchedules.stream()
                .filter(schedule -> schedule.getDepartureTime().isAfter(fromTime))
                .map(scheduleMapper::toScheduleResponse)
                .collect(Collectors.toList());
    }
}
