package com.transport.urbain.event.producer;

import com.transport.urbain.event.UserCreatedEvent;
import com.transport.urbain.event.UserDeletedEvent;
import com.transport.urbain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Event producer for publishing user-related events to Kafka.
 * 
 * <p>This component handles publishing user lifecycle events to Kafka topics
 * for asynchronous communication with other microservices in the system.
 * 
 * <p>Supported events:
 * <ul>
 *   <li>User Created - Published to 'user-created-events' topic</li>
 *   <li>User Updated - Published to 'user-updated-events' topic</li>
 *   <li>User Deleted - Published to 'user-deleted-events' topic</li>
 * </ul>
 * 
 * <p>Events are published with the user ID as the Kafka message key for proper
 * partitioning and ordering. Logging is included for monitoring and debugging purposes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private static final String USER_CREATED_TOPIC = "user-created-events";
    private static final String USER_UPDATED_TOPIC = "user-updated-events";
    private static final String USER_DELETED_TOPIC = "user-deleted-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a user created event to Kafka.
     * 
     * <p>Sends the event to the 'user-created-events' topic with the user ID as the key.
     * 
     * @param event the user created event to publish
     */
    public void publishUserCreated(UserCreatedEvent event) {
        log.info("Publishing user created event: {}", event);
        kafkaTemplate.send(USER_CREATED_TOPIC, event.getUserId().toString(), event);
    }

    /**
     * Publishes a user updated event to Kafka.
     * 
     * <p>Sends the event to the 'user-updated-events' topic with the user ID as the key.
     * 
     * @param event the user updated event to publish
     */
    public void publishUserUpdated(UserUpdatedEvent event) {
        log.info("Publishing user updated event: {}", event);
        kafkaTemplate.send(USER_UPDATED_TOPIC, event.getUserId().toString(), event);
    }

    /**
     * Publishes a user deleted event to Kafka.
     * 
     * <p>Sends the event to the 'user-deleted-events' topic with the user ID as the key.
     * 
     * @param event the user deleted event to publish
     */
    public void publishUserDeleted(UserDeletedEvent event) {
        log.info("Publishing user deleted event: {}", event);
        kafkaTemplate.send(USER_DELETED_TOPIC, event.getUserId().toString(), event);
    }
}