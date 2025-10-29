package com.transport.urbain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Config Server.
 * <p>
 * This configuration defines the security policies for the centralized
 * configuration server, ensuring that configuration data is protected
 * while allowing access to monitoring and encryption endpoints.
 * <p>
 * Security features:
 * <ul>
 *     <li>HTTP Basic Authentication for configuration access</li>
 *     <li>Public access to actuator endpoints for health monitoring</li>
 *     <li>Public access to encrypt/decrypt endpoints for configuration management</li>
 *     <li>CSRF protection disabled (stateless API)</li>
 *     <li>In-memory user authentication with BCrypt password encoding</li>
 * </ul>
 * <p>
 * Publicly accessible endpoints:
 * <ul>
 *     <li>/actuator/** - Health checks and metrics</li>
 *     <li>/encrypt/** - Configuration property encryption</li>
 *     <li>/decrypt/** - Configuration property decryption</li>
 * </ul>
 * <p>
 * Protected endpoints (require authentication):
 * <ul>
 *     <li>/{application}/{profile} - Application configuration endpoints</li>
 *     <li>/{application}/{profile}/{label} - Labeled configuration endpoints</li>
 *     <li>All other endpoints require authentication</li>
 * </ul>
 * <p>
 * Default credentials (change in production):
 * <ul>
 *     <li>Username: config</li>
 *     <li>Password: config123</li>
 *     <li>Role: ADMIN</li>
 * </ul>
 * <p>
 * <strong>WARNING:</strong> For production environments, replace the in-memory
 * authentication with a persistent user store (database) and use strong,
 * randomly generated passwords.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Security configuration includes:
     * <ul>
     *     <li>Disabled CSRF protection for stateless REST API</li>
     *     <li>Public access to actuator and encryption endpoints</li>
     *     <li>HTTP Basic Authentication for all other endpoints</li>
     *     <li>All other requests require authentication</li>
     * </ul>
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
                        .requestMatchers("/actuator/**", "/encrypt/**", "/decrypt/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }

    /**
     * Configures the user details service with in-memory authentication.
     * <p>
     * Creates a default user for accessing the configuration server.
     * This implementation uses an in-memory user store with a single
     * administrator user.
     * <p>
     * Default user:
     * <ul>
     *     <li>Username: config</li>
     *     <li>Password: config123 (BCrypt encoded)</li>
     *     <li>Role: ADMIN</li>
     * </ul>
     * <p>
     * <strong>Important:</strong> For production, implement a database-backed
     * UserDetailsService instead of using in-memory authentication.
     *
     * @return UserDetailsService configured with default user
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails configUser = User.builder()
                .username("config")
                .password(passwordEncoder().encode("config123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(configUser);
    }

    /**
     * Configures the password encoder for user authentication.
     * <p>
     * Uses BCrypt hashing algorithm for password encoding, which provides
     * strong security through adaptive hashing with salt. BCrypt automatically
     * handles salt generation and has a configurable computational cost.
     * <p>
     * BCrypt benefits:
     * <ul>
     *     <li>Built-in salt generation for each password</li>
     *     <li>Adaptive cost factor (adjustable for future hardware)</li>
     *     <li>Protection against rainbow table attacks</li>
     *     <li>Industry-standard secure password hashing</li>
     * </ul>
     *
     * @return BCryptPasswordEncoder for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
