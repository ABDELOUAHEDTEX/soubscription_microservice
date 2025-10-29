package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing an update to a user account.
 * 
 * <p>This event is published to Kafka when user information is updated in the system.
 * It notifies other microservices of changes to user data so they can keep their
 * local user information synchronized.
 * 
 * <p>The event is sent to the 'user-updated-events' Kafka topic and includes:
 * <ul>
 *   <li>User ID that was updated</li>
 *   <li>Updated email address</li>
 *   <li>Updated first and last name</li>
 *   <li>Timestamp of the update event</li>
 * </ul>
 * 
 * <p>Other services listening to this event should update their local user data
 * to maintain consistency across the microservices architecture.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime timestamp;
}
