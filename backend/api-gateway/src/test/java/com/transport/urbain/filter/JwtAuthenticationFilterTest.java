package com.transport.urbain.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.web.server.MockServerWebExchange.from;

/**
 * Unit tests for {@link JwtAuthenticationFilter}.
 * <p>
 * Tests verify JWT token validation, public endpoint bypassing,
 * and error handling scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private GatewayFilterChain filterChain;

    private String jwtSecret;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() throws Exception {
        jwtSecret = "6e7ac689c64392e6897fcbfb8a1a50c451c7fa7ed512189211f80c2de8cb556c";
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        // Set jwtSecret field using reflection since @Value doesn't work with @InjectMocks
        Field field = JwtAuthenticationFilter.class.getDeclaredField("jwtSecret");
        field.setAccessible(true);
        field.set(jwtAuthenticationFilter, jwtSecret);
    }

    @Test
    @DisplayName("Should allow requests to public endpoints without authentication")
    void testPublicEndpointWithoutAuth() {
        // Given
        var exchange = from(MockServerHttpRequest.get("/auth/login"));
        
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should allow requests to actuator endpoints without authentication")
    void testActuatorEndpointWithoutAuth() {
        // Given
        var exchange = from(MockServerHttpRequest.get("/actuator/health"));
        
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should reject request without Authorization header")
    void testRequestWithoutAuthHeader() {
        // Given
        var exchange = from(MockServerHttpRequest.get("/api/users/123"));

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("Should reject request with invalid Authorization header format")
    void testRequestWithInvalidAuthHeader() {
        // Given
        var exchange = from(MockServerHttpRequest.get("/api/users/123")
                .header(HttpHeaders.AUTHORIZATION, "InvalidFormat token123"));

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("Should accept request with valid JWT token")
    void testRequestWithValidToken() {
        // Given
        String token = createValidToken();
        var exchange = from(MockServerHttpRequest.get("/api/users/123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Should reject request with expired JWT token")
    void testRequestWithExpiredToken() {
        // Given
        String expiredToken = createExpiredToken();
        var exchange = from(MockServerHttpRequest.get("/api/users/123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken));

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("Should reject request with invalid JWT token signature")
    void testRequestWithInvalidTokenSignature() {
        // Given
        String invalidToken = "invalid.token.signature";
        var exchange = from(MockServerHttpRequest.get("/api/users/123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken));

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("Should reject request with malformed JWT token")
    void testRequestWithMalformedToken() {
        // Given
        String malformedToken = "malformed.token";
        var exchange = from(MockServerHttpRequest.get("/api/users/123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + malformedToken));

        // When
        Mono<Void> result = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())
                .filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    /**
     * Creates a valid JWT token for testing.
     *
     * @return valid JWT token string
     */
    private String createValidToken() {
        return Jwts.builder()
                .subject("user123")
                .claim("email", "user@example.com")
                .claim("role", "USER")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Creates an expired JWT token for testing.
     *
     * @return expired JWT token string
     */
    private String createExpiredToken() {
        return Jwts.builder()
                .subject("user123")
                .claim("email", "user@example.com")
                .claim("role", "USER")
                .issuedAt(Date.from(Instant.now().minusSeconds(3600)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(secretKey)
                .compact();
    }
}

