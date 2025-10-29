package com.transport.urbain.event.producer;

import com.transport.urbain.event.RouteChangedEvent;
import com.transport.urbain.event.ScheduleCreatedEvent;
import com.transport.urbain.event.ScheduleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer for publishing schedule-related events to Kafka topics.
 * <p>
 * This component handles publishing of domain events (schedule creation/updates,
 * route changes) to Kafka topics. Events are published asynchronously and consumed
 * by other microservices for real-time updates and system synchronization.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleEventProducer {

    /**
     * Kafka topic name for schedule creation events
     */
    private static final String SCHEDULE_CREATED_TOPIC = "schedule-created-events";

    /**
     * Kafka topic name for schedule update events
     */
    private static final String SCHEDULE_UPDATED_TOPIC = "schedule-updated-events";

    /**
     * Kafka topic name for route change events
     */
    private static final String ROUTE_CHANGED_TOPIC = "route-changed-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a schedule creation event to Kafka.
     * <p>
     * Sent when a new schedule is created in the system to notify subscribers
     * of the new schedule entry.
     *
     * @param event the schedule creation event
     */
    public void publishScheduleCreated(ScheduleCreatedEvent event) {
        log.info("Publishing schedule created event: {}", event);
        kafkaTemplate.send(SCHEDULE_CREATED_TOPIC, event.getScheduleId().toString(), event);
    }

    /**
     * Publishes a schedule update event to Kafka.
     * <p>
     * Sent when an existing schedule is modified to notify subscribers
     * of changes to timing, assignments, or status.
     *
     * @param event the schedule update event
     */
    public void publishScheduleUpdated(ScheduleUpdatedEvent event) {
        log.info("Publishing schedule updated event: {}", event);
        kafkaTemplate.send(SCHEDULE_UPDATED_TOPIC, event.getScheduleId().toString(), event);
    }

    /**
     * Publishes a route change event to Kafka.
     * <p>
     * Sent when a route is created or modified (e.g., stops added/removed) to
     * notify subscribers about route changes that may affect schedules and maps.
     *
     * @param event the route changed event
     */
    public void publishRouteChanged(RouteChangedEvent event) {
        log.info("Publishing route changed event: {}", event);
        kafkaTemplate.send(ROUTE_CHANGED_TOPIC, event.getRouteId().toString(), event);
    }
}
