package com.transport.urbain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 * 
 * <p>Configures Spring Security with JWT-based authentication and authorization.
 * This configuration sets up:
 * <ul>
 *   <li>Public endpoints (authentication, actuators, API documentation)</li>
 *   <li>JWT token filtering for protected endpoints</li>
 *   <li>Stateless session management</li>
 *   <li>Password encoding using BCrypt</li>
 *   <li>Custom authentication entry point for unauthorized access</li>
 * </ul>
 * 
 * <p>Security features:
 * <ul>
 *   <li>JWT authentication via JwtAuthenticationFilter</li>
 *   <li>Method-level security with @PreAuthorize</li>
 *   <li>CSRF protection disabled (REST API best practice)</li>
 *   <li>Stateless sessions (JWT-based)</li>
 * </ul>
 * 
 * <p>Public endpoints: /api/auth/**, /actuator/**, /v3/api-docs/**, /swagger-ui/**
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    /**
     * Configures the security filter chain with authentication and authorization rules.
     * 
     * <p>Sets up:
     * <ul>
     *   <li>Public endpoints that don't require authentication</li>
     *   <li>JWT authentication filter for protected endpoints</li>
     *   <li>Stateless session management</li>
     *   <li>Custom authentication entry point</li>
     * </ul>
     * 
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        return http.build();
    }

    /**
     * Configures the authentication provider with user details service and password encoder.
     * 
     * <p>Uses DAO authentication provider that:
     * <ul>
     *   <li>Loads users by username (email)</li>
     *   <li>Validates passwords using BCrypt</li>
     *   <li>Returns user details with authorities (roles)</li>
     * </ul>
     * 
     * @return the configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Provides the AuthenticationManager for the application.
     * 
     * @param config the AuthenticationConfiguration
     * @return the AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides the password encoder for hashing and validating passwords.
     * 
     * <p>Uses BCrypt with default strength (10 rounds) for password hashing.
     * 
     * @return the BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
