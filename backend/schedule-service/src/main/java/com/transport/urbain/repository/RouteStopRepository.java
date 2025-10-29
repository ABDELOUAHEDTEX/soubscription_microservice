package com.transport.urbain.repository;

import com.transport.urbain.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RouteStop entity operations.
 * <p>
 * Provides custom queries for managing route-stop associations including
 * sequence-based queries, counting stops on routes, and maintaining stop order.
 */
@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRouteIdOrderBySequenceNumberAsc(Long routeId);

    List<RouteStop> findByStopId(Long stopId);

    Optional<RouteStop> findByRouteIdAndStopId(Long routeId, Long stopId);

    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.sequenceNumber = :sequenceNumber")
    Optional<RouteStop> findByRouteIdAndSequenceNumber(
            @Param("routeId") Long routeId,
            @Param("sequenceNumber") Integer sequenceNumber
    );

    @Query("SELECT COUNT(rs) FROM RouteStop rs WHERE rs.route.id = :routeId")
    Integer countByRouteId(@Param("routeId") Long routeId);

    void deleteByRouteIdAndStopId(Long routeId, Long stopId);

    @Query("SELECT MAX(rs.sequenceNumber) FROM RouteStop rs WHERE rs.route.id = :routeId")
    Integer findMaxSequenceNumberByRouteId(@Param("routeId") Long routeId);
}
