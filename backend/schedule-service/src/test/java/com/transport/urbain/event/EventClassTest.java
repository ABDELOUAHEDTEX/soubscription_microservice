package com.transport.urbain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Event classes.
 * <p>
 * This test class covers event object structure including:
 * <ul>
 *     <li>ScheduleCreatedEvent fields and structure</li>
 *     <li>ScheduleUpdatedEvent fields and structure</li>
 *     <li>RouteChangedEvent fields and structure</li>
 *     <li>Event data validation</li>
 *     <li>Event equality and hashCode</li>
 * </ul>
 *
 * @author Transport Team
 */
class EventClassTest {

    /**
     * Tests ScheduleCreatedEvent constructor and getters.
     * Verifies that all fields are properly initialized and accessible.
     */
    @Test
    void testScheduleCreatedEvent_ConstructorAndGetters() {
        // Arrange
        Long scheduleId = 1L;
        Long routeId = 1L;
        String routeNumber = "R101";
        LocalTime departureTime = LocalTime.of(8, 0);
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        ScheduleCreatedEvent event = new ScheduleCreatedEvent(
                scheduleId, routeId, routeNumber, departureTime, timestamp
        );

        // Assert
        assertNotNull(event);
        assertEquals(scheduleId, event.getScheduleId());
        assertEquals(routeId, event.getRouteId());
        assertEquals(routeNumber, event.getRouteNumber());
        assertEquals(departureTime, event.getDepartureTime());
        assertEquals(timestamp, event.getTimestamp());
    }

    /**
     * Tests ScheduleCreatedEvent no-args constructor and setters.
     * Verifies that event object can be created and modified.
     */
    @Test
    void testScheduleCreatedEvent_SettersAndGetters() {
        // Arrange
        ScheduleCreatedEvent event = new ScheduleCreatedEvent();

        // Act
        event.setScheduleId(1L);
        event.setRouteId(1L);
        event.setRouteNumber("R101");
        event.setDepartureTime(LocalTime.of(8, 0));
        event.setTimestamp(LocalDateTime.now());

        // Assert
        assertEquals(1L, event.getScheduleId());
        assertEquals(1L, event.getRouteId());
        assertEquals("R101", event.getRouteNumber());
        assertNotNull(event.getDepartureTime());
        assertNotNull(event.getTimestamp());
    }

    /**
     * Tests ScheduleUpdatedEvent constructor and getters.
     * Verifies that all fields are properly initialized and accessible.
     */
    @Test
    void testScheduleUpdatedEvent_ConstructorAndGetters() {
        // Arrange
        Long scheduleId = 1L;
        Long routeId = 1L;
        String routeNumber = "R101";
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                scheduleId, routeId, routeNumber, timestamp
        );

        // Assert
        assertNotNull(event);
        assertEquals(scheduleId, event.getScheduleId());
        assertEquals(routeId, event.getRouteId());
        assertEquals(routeNumber, event.getRouteNumber());
        assertEquals(timestamp, event.getTimestamp());
    }

    /**
     * Tests ScheduleUpdatedEvent no-args constructor and setters.
     * Verifies that event object can be created and modified.
     */
    @Test
    void testScheduleUpdatedEvent_SettersAndGetters() {
        // Arrange
        ScheduleUpdatedEvent event = new ScheduleUpdatedEvent();

        // Act
        event.setScheduleId(1L);
        event.setRouteId(1L);
        event.setRouteNumber("R101");
        event.setTimestamp(LocalDateTime.now());

        // Assert
        assertEquals(1L, event.getScheduleId());
        assertEquals(1L, event.getRouteId());
        assertEquals("R101", event.getRouteNumber());
        assertNotNull(event.getTimestamp());
    }

    /**
     * Tests RouteChangedEvent constructor and getters.
     * Verifies that all fields are properly initialized and accessible.
     */
    @Test
    void testRouteChangedEvent_ConstructorAndGetters() {
        // Arrange
        Long routeId = 1L;
        String routeNumber = "R101";
        String routeName = "Downtown Express";
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        RouteChangedEvent event = new RouteChangedEvent(
                routeId, routeNumber, routeName, timestamp
        );

        // Assert
        assertNotNull(event);
        assertEquals(routeId, event.getRouteId());
        assertEquals(routeNumber, event.getRouteNumber());
        assertEquals(routeName, event.getRouteName());
        assertEquals(timestamp, event.getTimestamp());
    }

    /**
     * Tests RouteChangedEvent no-args constructor and setters.
     * Verifies that event object can be created and modified.
     */
    @Test
    void testRouteChangedEvent_SettersAndGetters() {
        // Arrange
        RouteChangedEvent event = new RouteChangedEvent();

        // Act
        event.setRouteId(1L);
        event.setRouteNumber("R101");
        event.setRouteName("Downtown Express");
        event.setTimestamp(LocalDateTime.now());

        // Assert
        assertEquals(1L, event.getRouteId());
        assertEquals("R101", event.getRouteNumber());
        assertEquals("Downtown Express", event.getRouteName());
        assertNotNull(event.getTimestamp());
    }

    /**
     * Tests event equality for ScheduleCreatedEvent.
     * Verifies that two events with same data are considered equal.
     */
    @Test
    void testEventEquality_ScheduleCreatedEvent() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        ScheduleCreatedEvent event1 = new ScheduleCreatedEvent(1L, 1L, "R101", LocalTime.of(8, 0), timestamp);
        ScheduleCreatedEvent event2 = new ScheduleCreatedEvent(1L, 1L, "R101", LocalTime.of(8, 0), timestamp);

        // Act & Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    /**
     * Tests event string representation.
     * Verifies that events have meaningful toString() output.
     */
    @Test
    void testEventToString() {
        // Arrange
        ScheduleCreatedEvent event = new ScheduleCreatedEvent();
        event.setScheduleId(1L);
        event.setRouteId(1L);
        event.setRouteNumber("R101");
        event.setDepartureTime(LocalTime.of(8, 0));
        event.setTimestamp(LocalDateTime.now());

        // Act
        String toString = event.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("ScheduleCreatedEvent"));
        assertTrue(toString.contains("scheduleId"));
    }
}

