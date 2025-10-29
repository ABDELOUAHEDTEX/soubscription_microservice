package com.transport.urbain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for the User Service microservice.
 * 
 * <p>This is a Spring Boot application that provides user management functionality
 * as part of the Urban Transport System. It handles:
 * <ul>
 *   <li>User registration and authentication</li>
 *   <li>User profile management</li>
 *   <li>JWT token generation and validation</li>
 *   <li>Role-based access control</li>
 * </ul>
 * 
 * <p>Enabled features:
 * <ul>
 *   <li>Spring Boot auto-configuration</li>
 *   <li>Service discovery client for microservices integration</li>
 *   <li>JPA auditing for automatic timestamp tracking</li>
 * </ul>
 * 
 * <p>The application integrates with:
 * <ul>
 *   <li>Service Registry (Eureka) for service discovery</li>
 *   <li>Config Server for centralized configuration</li>
 *   <li>Kafka for asynchronous event publishing</li>
 *   <li>Database for user data persistence</li>
 * </ul>
 * 
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.cloud.client.discovery.EnableDiscoveryClient
 * @see org.springframework.data.jpa.repository.config.EnableJpaAuditing
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class UserServiceApplication {
    /**
     * Main entry point for the User Service application.
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}