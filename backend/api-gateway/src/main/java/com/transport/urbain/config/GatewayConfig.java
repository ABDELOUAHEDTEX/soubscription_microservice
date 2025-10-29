package com.transport.urbain.config;

import com.transport.urbain.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routing configuration.
 * <p>
 * This configuration defines all routes for microservices and applies
 * cross-cutting concerns such as JWT authentication and circuit breakers.
 * <p>
 * Each route includes:
 * <ul>
 *     <li>Path matching for specific API endpoints</li>
 *     <li>JWT authentication filter for securing requests</li>
 *     <li>Circuit breaker for fault tolerance and resilience</li>
 *     <li>Fallback handling when services are unavailable</li>
 *     <li>Load-balanced routing using service discovery</li>
 * </ul>
 * <p>
 * Currently configured services:
 * <ul>
 *     <li>User Service - authentication and user management</li>
 *     <li>Ticket Service - ticket booking and management</li>
 *     <li>Schedule Service - routes, schedules, and stops</li>
 *     <li>Geolocation Service - location tracking and geolocation</li>
 *     <li>Subscription Service - subscription management</li>
 *     <li>Notification Service - notifications and alerts</li>
 * </ul>
 *
 */
@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor to inject JWT authentication filter.
     *
     * @param jwtAuthenticationFilter the JWT filter for securing routes
     */
    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Creates the route locator with all microservice routes.
     * <p>
     * This method defines the routing rules for all microservices in the system,
     * applying authentication and circuit breaker filters to each route.
     *
     * @param builder the route locator builder provided by Spring Cloud Gateway
     * @return RouteLocator configured with all service routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**", "/api/auth/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user-service"))
                        )
                        .uri("lb://user-service"))

                // Ticket Service Routes
                .route("ticket-service", r -> r
                        .path("/api/tickets/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("ticketServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/ticket-service"))
                        )
                        .uri("lb://ticket-service"))

                // Schedule Service Routes
                .route("schedule-service", r -> r
                        .path("/api/schedules/**", "/api/routes/**", "/api/stops/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("scheduleServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/schedule-service"))
                        )
                        .uri("lb://schedule-service"))

                // Geolocation Service Routes
                .route("geolocation-service", r -> r
                        .path("/api/geolocation/**", "/api/tracking/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("geolocationServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/geolocation-service"))
                        )
                        .uri("lb://geolocation-service"))

                // Subscription Service Routes
                .route("subscription-service", r -> r
                        .path("/api/subscriptions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("subscriptionServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/subscription-service"))
                        )
                        .uri("lb://subscription-service"))

                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("notificationServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/notification-service"))
                        )
                        .uri("lb://notification-service"))

                .build();
    }
}
