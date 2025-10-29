package com.transport.urbain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Schedule Service.
 * <p>
 * This configuration enables method-level security using @PreAuthorize annotations
 * to protect endpoints based on user roles. The actual authentication is delegated
 * to the API Gateway which validates JWT tokens.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Security configuration includes:
     * <ul>
     *     <li>Disabled CSRF protection for stateless REST API</li>
     *     <li>All requests are permitted without authentication</li>
     *     <li>Method-level security is enforced via @PreAuthorize annotations</li>
     *     <li>Role-based access control is enforced at the method level</li>
     * </ul>
     * <p>
     * Note: The API Gateway handles JWT token validation before forwarding
     * requests to this service. The roles are extracted from the token and
     * used by @PreAuthorize annotations.
     *
     * @param http HttpSecurity object to configure
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
