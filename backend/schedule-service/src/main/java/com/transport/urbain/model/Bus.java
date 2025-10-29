package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a bus in the transport fleet.
 * <p>
 * Contains bus specifications, amenities, operational status, maintenance records,
 * and relationships to schedules. Supports fleet management and operational planning.
 */
@Entity
@Table(name = "buses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Bus {

    /**
     * Unique identifier for the bus
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique bus number/identifier (e.g., "BUS-001")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String busNumber;

    /**
     * Vehicle license plate number
     */
    @Column(nullable = false, unique = true, length = 50)
    private String licensePlate;

    /**
     * Bus model name
     */
    @Column(nullable = false, length = 100)
    private String model;

    /**
     * Manufacturer name
     */
    @Column(nullable = false, length = 100)
    private String manufacturer;

    /**
     * Manufacturing year
     */
    @Column(nullable = false)
    private Integer year;

    /**
     * Total passenger capacity
     */
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Number of seats available
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer seatingCapacity = 0;

    /**
     * Number of standing spaces available
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer standingCapacity = 0;

    /**
     * Current operational status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatus status;

    /**
     * Whether the bus has WiFi capability
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasWifi = false;

    /**
     * Whether the bus has air conditioning
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasAirConditioning = false;

    /**
     * Whether the bus is wheelchair accessible
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccessible = false;

    /**
     * Whether the bus has GPS tracking enabled
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasGPS = true;

    /**
     * Date of last maintenance performed
     */
    private LocalDateTime lastMaintenanceDate;

    /**
     * Date of next scheduled maintenance
     */
    private LocalDateTime nextMaintenanceDate;

    /**
     * Additional notes or comments
     */
    @Column(length = 1000)
    private String notes;

    /**
     * List of schedules assigned to this bus
     */
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    /**
     * Timestamp when the bus was created
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the bus information was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
