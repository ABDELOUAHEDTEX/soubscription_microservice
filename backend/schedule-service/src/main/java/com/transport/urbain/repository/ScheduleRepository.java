package com.transport.urbain.repository;

import com.transport.urbain.model.DayOfWeek;
import com.transport.urbain.model.Schedule;
import com.transport.urbain.model.ScheduleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for Schedule entity operations.
 * <p>
 * Provides custom queries for schedule management including date-based filtering,
 * time range queries, conflict detection, and operational day filtering.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findByRouteId(Long routeId, Pageable pageable);

    Page<Schedule> findByBusId(Long busId, Pageable pageable);

    Page<Schedule> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId AND s.isActive = true " +
            "AND (s.validFrom IS NULL OR s.validFrom <= :date) " +
            "AND (s.validUntil IS NULL OR s.validUntil >= :date) " +
            "AND :dayOfWeek MEMBER OF s.daysOfWeek " +
            "ORDER BY s.departureTime")
    List<Schedule> findActiveSchedulesForRouteAndDate(
            @Param("routeId") Long routeId,
            @Param("date") LocalDate date,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

    @Query("SELECT s FROM Schedule s WHERE s.isActive = true " +
            "AND (s.validFrom IS NULL OR s.validFrom <= :date) " +
            "AND (s.validUntil IS NULL OR s.validUntil >= :date) " +
            "AND :dayOfWeek MEMBER OF s.daysOfWeek " +
            "AND s.departureTime BETWEEN :fromTime AND :toTime " +
            "ORDER BY s.departureTime")
    List<Schedule> findSchedulesByDateAndTimeRange(
            @Param("date") LocalDate date,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("fromTime") LocalTime fromTime,
            @Param("toTime") LocalTime toTime
    );

    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId " +
            "AND s.scheduleType = :scheduleType AND s.isActive = true")
    List<Schedule> findByRouteIdAndScheduleType(
            @Param("routeId") Long routeId,
            @Param("scheduleType") ScheduleType scheduleType
    );

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.bus.id = :busId " +
            "AND s.isActive = true " +
            "AND (s.validFrom IS NULL OR s.validFrom <= :date) " +
            "AND (s.validUntil IS NULL OR s.validUntil >= :date)")
    Long countActiveSchedulesForBus(@Param("busId") Long busId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.bus.id = :busId " +
            "AND s.isActive = true " +
            "AND (s.validFrom IS NULL OR s.validFrom <= :date) " +
            "AND (s.validUntil IS NULL OR s.validUntil >= :date) " +
            "AND :dayOfWeek MEMBER OF s.daysOfWeek " +
            "AND ((s.departureTime <= :endTime AND s.arrivalTime >= :startTime))")
    List<Schedule> findConflictingSchedules(
            @Param("busId") Long busId,
            @Param("date") LocalDate date,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
