package com.transport.urbain.repository;

import com.transport.urbain.model.Stop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Stop entity operations.
 * <p>
 * Provides custom queries for stop management including location-based searches,
 * nearest stop queries using geospatial calculations, and accessibility filtering.
 */
@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {

    Optional<Stop> findByStopCode(String stopCode);

    Boolean existsByStopCode(String stopCode);

    Page<Stop> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Stop s WHERE s.isActive = true")
    Page<Stop> findAllActiveStops(Pageable pageable);

    @Query("SELECT s FROM Stop s WHERE LOWER(s.stopName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.stopCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Stop> searchStops(@Param("keyword") String keyword, Pageable pageable);

    List<Stop> findByCity(String city);

    @Query("SELECT s FROM Stop s WHERE s.isActive = true " +
            "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(s.latitude)))) < :radius " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(s.latitude))))")
    List<Stop> findNearbyStops(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radius") Double radius
    );

    @Query("SELECT s FROM Stop s WHERE s.isAccessible = true AND s.isActive = true")
    List<Stop> findAllAccessibleStops();
}