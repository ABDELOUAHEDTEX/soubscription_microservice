package com.transport.urbain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Schedule Service microservice.
 * <p>
 * This service manages bus schedules, routes, stops, and buses in the Urban Transport System.
 * It provides RESTful APIs for:
 * <ul>
 *     <li>Bus fleet management</li>
 *     <li>Route creation and management</li>
 *     <li>Stop location and accessibility management</li>
 *     <li>Schedule creation, updates, and queries</li>
 * </ul>
 * <p>
 * Features enabled:
 * <ul>
 *     <li>Service Discovery: Registers with Eureka for microservice communication</li>
 *     <li>JPA Auditing: Automatically manages createdAt and updatedAt timestamps</li>
 *     <li>Caching: Redis-based caching for improved performance</li>
 *     <li>Scheduling: Supports scheduled tasks and background operations</li>
 * </ul>
 *
 * @author Transport Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class ScheduleServiceApplication {

    /**
     * Main entry point for the Schedule Service application.
     * <p>
     * Initializes Spring Boot context and starts the embedded server.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ScheduleServiceApplication.class, args);
    }
}