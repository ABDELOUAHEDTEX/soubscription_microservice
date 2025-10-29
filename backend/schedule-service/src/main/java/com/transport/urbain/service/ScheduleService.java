package com.transport.urbain.service;

import com.transport.urbain.dto.request.CreateScheduleRequest;
import com.transport.urbain.dto.request.SearchScheduleRequest;
import com.transport.urbain.dto.request.UpdateScheduleRequest;
import com.transport.urbain.dto.response.ScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;

/**
 * Service interface for schedule management operations.
 * <p>
 * Provides methods for CRUD operations on bus schedules, including creation, retrieval,
 * updating, deletion, and activation/deactivation. Supports time-based queries,
 * route filtering, and schedule search functionality.
 */
public interface ScheduleService {

    ScheduleResponse createSchedule(CreateScheduleRequest request);

    ScheduleResponse getScheduleById(Long id);

    Page<ScheduleResponse> getAllSchedules(Pageable pageable);

    List<ScheduleResponse> getSchedulesByRoute(Long routeId);

    Page<ScheduleResponse> getActiveSchedules(Pageable pageable);

    List<ScheduleResponse> searchSchedules(SearchScheduleRequest request);

    List<ScheduleResponse> getTodaySchedules(Long routeId);

    List<ScheduleResponse> getUpcomingSchedules(Long routeId, LocalTime fromTime);

    ScheduleResponse updateSchedule(Long id, UpdateScheduleRequest request);

    void deleteSchedule(Long id);

    void activateSchedule(Long id);

    void deactivateSchedule(Long id);
}
