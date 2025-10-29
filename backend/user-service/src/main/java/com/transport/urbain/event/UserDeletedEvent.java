package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing the deletion of a user account.
 * 
 * <p>This event is published to Kafka when a user account is permanently deleted
 * from the system. It notifies other microservices that the user no longer exists
 * and they should clean up any user-specific data.
 * 
 * <p>The event is sent to the 'user-deleted-events' Kafka topic and includes:
 * <ul>
 *   <li>User ID that was deleted</li>
 *   <li>Email address of the deleted user</li>
 *   <li>Timestamp of the deletion event</li>
 * </ul>
 * 
 * <p>Other services listening to this event should:
 * <ul>
 *   <li>Delete or anonymize user data</li>
 *   <li>Cancel any active subscriptions or services</li>
 *   <li>Archive or remove historical data as per data retention policies</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    private Long userId;
    private String email;
    private LocalDateTime timestamp;
}
