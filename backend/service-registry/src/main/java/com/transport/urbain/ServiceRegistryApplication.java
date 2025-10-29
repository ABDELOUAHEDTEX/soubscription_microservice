package com.transport.urbain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main entry point for the Service Registry (Eureka Server).
 * <p>
 * The Service Registry provides service discovery capabilities for all
 * microservices in the Urban Transport System. It maintains a registry of
 * available service instances and their health status.
 * <p>
 * Key features:
 * <ul>
 *     <li>Service registration and discovery for all microservices</li>
 *     <li>Health monitoring and automatic deregistration of failed services</li>
 *     <li>Load balancing support through service instance selection</li>
 *     <li>RESTful API for querying registered services</li>
 *     <li>Peer awareness for high availability (multi-instance support)</li>
 * </ul>
 * <p>
 * Registered services:
 * <ul>
 *     <li>API Gateway - Entry point for all requests</li>
 *     <li>Config Server - Centralized configuration management</li>
 *     <li>User Service - User management and authentication</li>
 *     <li>Ticket Service - Ticket booking and management</li>
 *     <li>Schedule Service - Routes, schedules, and stops</li>
 *     <li>Geolocation Service - Location tracking and geolocation</li>
 *     <li>Subscription Service - Subscription management</li>
 *     <li>Notification Service - Notifications and alerts</li>
 * </ul>
 * <p>
 * Public endpoints:
 * <ul>
 *     <li>/actuator/** - Health and metrics endpoints (public)</li>
 * </ul>
 * <p>
 * Secured endpoints (HTTP Basic Authentication):
 * <ul>
 *     <li>/eureka/** - Eureka dashboard and service registry access</li>
 *     <li>All other endpoints require authentication</li>
 * </ul>
 * <p>
 * Default credentials for development:
 * <ul>
 *     <li>Username: eureka</li>
 *     <li>Password: eureka123</li>
 *     <li>Role: ADMIN</li>
 * </ul>
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {
    
    /**
     * Main method to start the Service Registry application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}