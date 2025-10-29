package com.transport.urbain.repository;

import com.transport.urbain.model.Bus;
import com.transport.urbain.model.BusStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Bus entity operations.
 * <p>
 * Provides custom queries for bus management including status-based filtering,
 * search functionality, maintenance tracking, and accessibility queries.
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByBusNumber(String busNumber);

    Optional<Bus> findByLicensePlate(String licensePlate);

    Boolean existsByBusNumber(String busNumber);

    Boolean existsByLicensePlate(String licensePlate);

    Page<Bus> findByStatus(BusStatus status, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.status = 'ACTIVE' OR b.status = 'IN_SERVICE'")
    Page<Bus> findAllActiveBuses(Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE LOWER(b.busNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Bus> searchBuses(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.nextMaintenanceDate <= :date AND b.status != 'MAINTENANCE'")
    List<Bus> findBusesDueForMaintenance(@Param("date") LocalDateTime date);

    List<Bus> findByManufacturer(String manufacturer);

    @Query("SELECT b FROM Bus b WHERE b.isAccessible = true AND (b.status = 'ACTIVE' OR b.status = 'IN_SERVICE')")
    List<Bus> findAccessibleBuses();

    @Query("SELECT COUNT(b) FROM Bus b WHERE b.status = :status")
    Long countByStatus(@Param("status") BusStatus status);
}
