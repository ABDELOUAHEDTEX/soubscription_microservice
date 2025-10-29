package com.transport.urbain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Main entry point for the Config Server service.
 * <p>
 * The Config Server provides centralized configuration management for all
 * microservices in the Urban Transport System. It serves as a single source
 * of truth for application properties, allowing dynamic configuration updates
 * without requiring service restarts.
 * <p>
 * Key features:
 * <ul>
 *     <li>Centralized configuration management for all microservices</li>
 *     <li>Environment-specific configurations (dev, test, prod)</li>
 *     <li>Encrypted sensitive properties using Spring Cloud Config encryption</li>
 *     <li>Native Git repository backend for version control</li>
 *     <li>RESTful API for accessing configurations</li>
 * </ul>
 * <p>
 * Public endpoints:
 * <ul>
 *     <li>/actuator/** - Health and metrics endpoints (public)</li>
 *     <li>/encrypt/** - Configuration encryption endpoints (public)</li>
 *     <li>/decrypt/** - Configuration decryption endpoints (public)</li>
 * </ul>
 * <p>
 * Secured endpoints (HTTP Basic Authentication):
 * <ul>
 *     <li>/{application}/{profile}[/{label}] - Application configuration access</li>
 *     <li>/{application}/{profile}[/{label}]/{path} - Specific property access</li>
 * </ul>
 * <p>
 * Default credentials for development:
 * <ul>
 *     <li>Username: config</li>
 *     <li>Password: config123</li>
 *     <li>Role: ADMIN</li>
 * </ul>
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    
    /**
     * Main method to start the Config Server application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}