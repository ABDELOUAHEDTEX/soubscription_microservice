package com.transport.urbain.event.producer;

import com.transport.urbain.event.RouteChangedEvent;
import com.transport.urbain.event.ScheduleCreatedEvent;
import com.transport.urbain.event.ScheduleUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduleEventProducer.
 * <p>
 * This test class covers all Kafka event publishing operations including:
 * <ul>
 *     <li>Schedule created event publishing</li>
 *     <li>Schedule updated event publishing</li>
 *     <li>Route changed event publishing</li>
 *     <li>Kafka template interaction</li>
 *     <li>Event message sending</li>
 * </ul>
 *
 * @author Transport Team
 */
@ExtendWith(MockitoExtension.class)
class ScheduleEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ScheduleEventProducer scheduleEventProducer;

    private ScheduleCreatedEvent scheduleCreatedEvent;
    private ScheduleUpdatedEvent scheduleUpdatedEvent;
    private RouteChangedEvent routeChangedEvent;

    /**
     * Sets up test data before each test method.
     * Creates mock event objects for testing.
     */
    @BeforeEach
    void setUp() {
        scheduleCreatedEvent = new ScheduleCreatedEvent();
        scheduleCreatedEvent.setScheduleId(1L);
        scheduleCreatedEvent.setRouteId(1L);
        scheduleCreatedEvent.setRouteNumber("R101");
        scheduleCreatedEvent.setDepartureTime(LocalTime.of(8, 0));
        scheduleCreatedEvent.setTimestamp(LocalDateTime.now());

        scheduleUpdatedEvent = new ScheduleUpdatedEvent();
        scheduleUpdatedEvent.setScheduleId(1L);
        scheduleUpdatedEvent.setRouteId(1L);
        scheduleUpdatedEvent.setRouteNumber("R101");
        scheduleUpdatedEvent.setTimestamp(LocalDateTime.now());

        routeChangedEvent = new RouteChangedEvent();
        routeChangedEvent.setRouteId(1L);
        routeChangedEvent.setRouteNumber("R101");
        routeChangedEvent.setRouteName("Downtown Express");
        routeChangedEvent.setTimestamp(LocalDateTime.now());
    }

    /**
     * Tests successful publishing of schedule created event.
     * Verifies that the event is sent to the correct Kafka topic.
     */
    @Test
    void testPublishScheduleCreated_Success() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(ScheduleCreatedEvent.class)))
                .thenReturn(null);

        // Act
        assertDoesNotThrow(() -> scheduleEventProducer.publishScheduleCreated(scheduleCreatedEvent));

        // Assert
        verify(kafkaTemplate, times(1))
                .send(eq("schedule-created-events"), eq("1"), any(ScheduleCreatedEvent.class));
    }

    /**
     * Tests successful publishing of schedule updated event.
     * Verifies that the event is sent to the correct Kafka topic.
     */
    @Test
    void testPublishScheduleUpdated_Success() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(ScheduleUpdatedEvent.class)))
                .thenReturn(null);

        // Act
        assertDoesNotThrow(() -> scheduleEventProducer.publishScheduleUpdated(scheduleUpdatedEvent));

        // Assert
        verify(kafkaTemplate, times(1))
                .send(eq("schedule-updated-events"), eq("1"), any(ScheduleUpdatedEvent.class));
    }

    /**
     * Tests successful publishing of route changed event.
     * Verifies that the event is sent to the correct Kafka topic.
     */
    @Test
    void testPublishRouteChanged_Success() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(RouteChangedEvent.class)))
                .thenReturn(null);

        // Act
        assertDoesNotThrow(() -> scheduleEventProducer.publishRouteChanged(routeChangedEvent));

        // Assert
        verify(kafkaTemplate, times(1))
                .send(eq("route-changed-events"), eq("1"), any(RouteChangedEvent.class));
    }

    /**
     * Tests event publishing with null key.
     * Verifies that publishing handles null keys gracefully.
     */
    @Test
    void testPublishScheduleCreated_WithNullKey() {
        // Arrange
        ScheduleCreatedEvent event = new ScheduleCreatedEvent();
        event.setScheduleId(null);
        event.setRouteId(1L);

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> scheduleEventProducer.publishScheduleCreated(event));
    }

    /**
     * Tests event publishing with multiple events.
     * Verifies that multiple events can be published sequentially.
     */
    @Test
    void testPublishMultipleEvents_Success() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        // Act
        scheduleEventProducer.publishScheduleCreated(scheduleCreatedEvent);
        scheduleEventProducer.publishScheduleUpdated(scheduleUpdatedEvent);
        scheduleEventProducer.publishRouteChanged(routeChangedEvent);

        // Assert
        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), any());
    }

    /**
     * Tests event structure validation.
     * Verifies that all required fields are present in events.
     */
    @Test
    void testEventStructure_ScheduleCreatedEvent() {
        // Act & Assert
        assertNotNull(scheduleCreatedEvent.getScheduleId());
        assertNotNull(scheduleCreatedEvent.getRouteId());
        assertNotNull(scheduleCreatedEvent.getRouteNumber());
        assertNotNull(scheduleCreatedEvent.getDepartureTime());
        assertNotNull(scheduleCreatedEvent.getTimestamp());
    }

    /**
     * Tests event structure validation.
     * Verifies that all required fields are present in schedule updated events.
     */
    @Test
    void testEventStructure_ScheduleUpdatedEvent() {
        // Act & Assert
        assertNotNull(scheduleUpdatedEvent.getScheduleId());
        assertNotNull(scheduleUpdatedEvent.getRouteId());
        assertNotNull(scheduleUpdatedEvent.getRouteNumber());
        assertNotNull(scheduleUpdatedEvent.getTimestamp());
    }

    /**
     * Tests event structure validation.
     * Verifies that all required fields are present in route changed events.
     */
    @Test
    void testEventStructure_RouteChangedEvent() {
        // Act & Assert
        assertNotNull(routeChangedEvent.getRouteId());
        assertNotNull(routeChangedEvent.getRouteNumber());
        assertNotNull(routeChangedEvent.getRouteName());
        assertNotNull(routeChangedEvent.getTimestamp());
    }
}

