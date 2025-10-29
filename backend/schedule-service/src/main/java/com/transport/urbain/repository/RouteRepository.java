package com.transport.urbain.repository;

import com.transport.urbain.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Route entity operations.
 * <p>
 * Provides custom queries for route management including search functionality,
 * filtering by stops, and circular route identification.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteNumber(String routeNumber);

    Boolean existsByRouteNumber(String routeNumber);

    Page<Route> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT r FROM Route r WHERE r.isActive = true")
    Page<Route> findAllActiveRoutes(Pageable pageable);

    @Query("SELECT r FROM Route r WHERE LOWER(r.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.origin) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.destination) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Route> searchRoutes(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Route r JOIN r.routeStops rs WHERE rs.stop.id = :stopId")
    Page<Route> findByStopId(@Param("stopId") Long stopId, Pageable pageable);

    @Query("SELECT r FROM Route r WHERE r.isCircular = true AND r.isActive = true")
    List<Route> findAllCircularRoutes();
}