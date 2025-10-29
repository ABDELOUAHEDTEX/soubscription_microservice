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
 * Configuration class for Kafka Producer.
 * 
 * <p>This class configures the Kafka producer factory and template for sending
 * messages to Kafka topics. It uses String serialization for keys and JSON
 * serialization for values, with type information headers disabled.
 * 
 * <p>The configuration supports publishing events such as user registration,
 * profile updates, and other user-related events to Kafka topics for
 * asynchronous processing by other microservices.
 * 
 * <p>The bootstrap servers configuration is injected from application properties
 * using the {@value spring.kafka.bootstrap-servers} property.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates and configures the Kafka producer factory.
     * 
     * <p>Configures the producer with:
     * <ul>
     *   <li>Bootstrap servers for Kafka cluster connection</li>
     *   <li>String serializer for message keys</li>
     *   <li>JSON serializer for message values</li>
     *   <li>Type information headers disabled</li>
     * </ul>
     * 
     * @return configured ProducerFactory instance
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates and configures the Kafka template for sending messages.
     * 
     * <p>This template provides a convenient API for sending messages to Kafka topics
     * and is used throughout the application for event publishing.
     * 
     * @return configured KafkaTemplate instance
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
