package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a bus schedule entry.
 * <p>
 * Defines when and how a bus operates on a specific route, including timing,
 * operational days, validity periods, and bus assignment. Supports flexible
 * scheduling with frequency-based or single-trip operations.
 */
@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    /**
     * Unique identifier for the schedule
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Route on which this schedule operates
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    /**
     * Bus assigned to this schedule (optional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    /**
     * Departure time from the origin stop
     */
    @Column(nullable = false)
    private LocalTime departureTime;

    /**
     * Arrival time at the destination stop
     */
    @Column(nullable = false)
    private LocalTime arrivalTime;

    /**
     * Type of schedule (REGULAR, EXPRESS, WEEKEND, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleType scheduleType;

    /**
     * Days of the week when this schedule operates
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "schedule_days", joinColumns = @JoinColumn(name = "schedule_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    @Builder.Default
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();

    /**
     * Date from which this schedule becomes valid
     */
    private LocalDate validFrom;

    /**
     * Date until which this schedule is valid
     */
    private LocalDate validUntil;

    /**
     * Whether the schedule is currently active
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Frequency of operation in minutes (0 means single trip, not recurring)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer frequency = 0;

    /**
     * Additional notes about the schedule
     */
    @Column(length = 500)
    private String notes;

    /**
     * Timestamp when the schedule was created
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the schedule was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
