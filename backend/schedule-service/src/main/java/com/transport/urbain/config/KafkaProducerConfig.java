package com.transport.urbain.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka producer configuration for the Schedule Service.
 * <p>
 * This configuration class sets up Kafka producers to send messages to Kafka topics.
 * It configures serializers for message keys (String) and values (JSON), enabling
 * the service to publish events and notifications to other microservices.
 * <p>
 * The configuration uses:
 * <ul>
 *     <li>String serializer for message keys</li>
 *     <li>JSON serializer for message values</li>
 *     <li>Bootstrap servers from application configuration</li>
 * </ul>
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * Kafka bootstrap servers address.
     * Injected from application configuration (e.g., application.yml).
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates and configures the Kafka producer factory.
     * <p>
     * This factory is responsible for creating Kafka producers with the following settings:
     * <ul>
     *     <li>Bootstrap servers configuration</li>
     *     <li>String serializer for message keys</li>
     *     <li>JSON serializer for message values</li>
     *     <li>Disabled type information headers to reduce message size</li>
     * </ul>
     *
     * @return configured ProducerFactory for String keys and Object values
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Configure bootstrap servers (Kafka broker addresses)
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Configure key serializer (String)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Configure value serializer (JSON)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Disable type information headers to keep messages compact
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a Kafka template for sending messages to Kafka topics.
     * <p>
     * This template provides a high-level API for sending messages to Kafka.
     * It's used throughout the application to publish events and notifications.
     * <p>
     * Example usage:
     * <pre>
     * kafkaTemplate.send("schedule-events", "key", eventObject);
     * </pre>
     *
     * @return KafkaTemplate configured with the producer factory
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
