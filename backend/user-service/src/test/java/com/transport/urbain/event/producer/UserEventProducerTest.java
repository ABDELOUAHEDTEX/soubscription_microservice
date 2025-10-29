package com.transport.urbain.event.producer;

import com.transport.urbain.event.UserCreatedEvent;
import com.transport.urbain.event.UserDeletedEvent;
import com.transport.urbain.event.UserUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserEventProducer.
 * 
 * <p>Tests Kafka event publishing for user lifecycle events including:
 * <ul>
 *   <li>User created event publishing</li>
 *   <li>User updated event publishing</li>
 *   <li>User deleted event publishing</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Correct Kafka topic usage</li>
 *   <li>User ID as message key</li>
 *   <li>Event data transmission</li>
 *   <li>Kafka template interaction</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private UserEventProducer userEventProducer;

    @Test
    void shouldPublishUserCreatedEvent() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                1L,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now()
        );

        CompletableFuture<SendResult<String, Object>> future = 
                CompletableFuture.completedFuture(new SendResult<>(null, null));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        userEventProducer.publishUserCreated(event);

        // Then
        verify(kafkaTemplate).send(eq("user-created-events"), eq("1"), eq(event));
    }

    @Test
    void shouldPublishUserUpdatedEvent() {
        // Given
        UserUpdatedEvent event = new UserUpdatedEvent(
                1L,
                "test@example.com",
                "Jane",
                "Smith",
                LocalDateTime.now()
        );

        CompletableFuture<SendResult<String, Object>> future = 
                CompletableFuture.completedFuture(new SendResult<>(null, null));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        userEventProducer.publishUserUpdated(event);

        // Then
        verify(kafkaTemplate).send(eq("user-updated-events"), eq("1"), eq(event));
    }

    @Test
    void shouldPublishUserDeletedEvent() {
        // Given
        UserDeletedEvent event = new UserDeletedEvent(
                1L,
                "test@example.com",
                LocalDateTime.now()
        );

        CompletableFuture<SendResult<String, Object>> future = 
                CompletableFuture.completedFuture(new SendResult<>(null, null));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        userEventProducer.publishUserDeleted(event);

        // Then
        verify(kafkaTemplate).send(eq("user-deleted-events"), eq("1"), eq(event));
    }

    @Test
    void shouldUseUserIdAsKafkaKey() {
        // Given
        Long userId = 42L;
        UserCreatedEvent event = new UserCreatedEvent(
                userId,
                "user@example.com",
                "Test",
                "User",
                LocalDateTime.now()
        );

        CompletableFuture<SendResult<String, Object>> future = 
                CompletableFuture.completedFuture(new SendResult<>(null, null));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        userEventProducer.publishUserCreated(event);

        // Then
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("user-created-events"), keyCaptor.capture(), eq(event));
        assertThat(keyCaptor.getValue()).isEqualTo("42");
    }
}

