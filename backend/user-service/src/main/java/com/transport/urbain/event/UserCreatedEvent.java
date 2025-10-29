package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing the creation of a new user account.
 * 
 * <p>This event is published to Kafka when a new user successfully registers
 * in the system. It contains the essential user information needed by other
 * microservices that need to be notified of user creation.
 * 
 * <p>The event is sent to the 'user-created-events' Kafka topic and includes:
 * <ul>
 *   <li>User ID for reference</li>
 *   <li>Email address</li>
 *   <li>User's first and last name</li>
 *   <li>Timestamp of the event</li>
 * </ul>
 * 
 * <p>Other services listening to this event can use it to initialize user-specific
 * data or perform any necessary setup for the new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime timestamp;
}
