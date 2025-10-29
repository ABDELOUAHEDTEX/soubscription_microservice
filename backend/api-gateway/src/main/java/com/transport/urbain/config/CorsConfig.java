package com.transport.urbain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the API Gateway.
 * <p>
 * This configuration allows specific origins, methods, and headers to access
 * the API Gateway, enabling frontend applications to make cross-origin requests.
 * <p>
 * The configuration allows:
 * <ul>
 *     <li>Origins: localhost:3000 and localhost:3001 (development ports)</li>
 *     <li>Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS</li>
 *     <li>Headers: All headers are allowed (*)</li>
 *     <li>Credentials: Enabled for authenticated requests</li>
 *     <li>Max Age: 3600 seconds (1 hour)</li>
 * </ul>
 *
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS web filter bean for reactive Spring WebFlux.
     * <p>
     * This filter automatically handles CORS preflight and actual requests
     * based on the configured policy.
     *
     * @return CorsWebFilter configured with allowed origins, methods, and headers
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
