package com.transport.urbain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the API Gateway service.
 * <p>
 * The API Gateway serves as the single entry point for all client requests,
 * providing centralized routing, authentication, and cross-cutting concerns
 * such as CORS handling, circuit breakers, and fallback mechanisms.
 * <p>
 * This gateway integrates with:
 * <ul>
 *     <li>Eureka Service Discovery for dynamic service routing</li>
 *     <li>JWT-based authentication for securing microservices</li>
 *     <li>Circuit breakers for resilience and fault tolerance</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    /**
     * Main method to start the API Gateway application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}