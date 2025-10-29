package com.transport.urbain.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT Authentication Filter for Spring Cloud Gateway.
 * <p>
 * This filter validates JWT tokens on incoming requests and extracts user information
 * from the token claims to forward authenticated user context to downstream services.
 * <p>
 * Key features:
 * <ul>
 *     <li>Validates JWT tokens using HMAC-SHA algorithm</li>
 *     <li>Skips authentication for public endpoints (auth, actuator, public paths)</li>
 *     <li>Extracts user information from token claims (userId, email, role)</li>
 *     <li>Adds custom headers to downstream requests with user context</li>
 *     <li>Returns 401 Unauthorized for invalid or missing tokens</li>
 * </ul>
 * <p>
 * Public endpoints that bypass authentication:
 * <ul>
 *     <li>/auth/** - Authentication endpoints</li>
 *     <li>/actuator/** - Spring Actuator endpoints</li>
 *     <li>/public/** - Publicly accessible resources</li>
 *     <li>/ - Root path</li>
 * </ul>
 * <p>
 * Custom headers added to authenticated requests:
 * <ul>
 *     <li>X-User-Id: User ID from token subject</li>
 *     <li>X-User-Email: User email from token claims</li>
 *     <li>X-User-Role: User role from token claims</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    /** JWT secret key for token validation (default: fallback key) */
    @Value("${jwt.secret:6e7ac689c64392e6897fcbfb8a1a50c451c7fa7ed512189211f80c2de8cb556c}")
    private String jwtSecret;

    /**
     * Default constructor for the JWT authentication filter.
     */
    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Applies JWT authentication to gateway requests.
     * <p>
     * This method creates a gateway filter that:
     * <ol>
     *     <li>Checks if the request path is a public endpoint (skips auth if yes)</li>
     *     <li>Validates the presence of Authorization header</li>
     *     <li>Extracts and validates the JWT token</li>
     *     <li>Adds user context to request headers</li>
     *     <li>Continues to downstream services or returns error</li>
     * </ol>
     *
     * @param config filter configuration (currently unused, reserved for future use)
     * @return GatewayFilter that performs JWT authentication
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip authentication for public endpoints
            if (isPublicEndpoint(request.getPath().toString())) {
                return chain.filter(exchange);
            }

            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = validateToken(token);

                // Add user information to request headers
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-User-Email", claims.get("email", String.class))
                        .header("X-User-Role", claims.get("role", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Validates the JWT token and extracts claims.
     * <p>
     * Parses the token using the configured secret key and returns the claims.
     * Throws exceptions for invalid, expired, or malformed tokens.
     *
     * @param token the JWT token string to validate
     * @return Claims object containing token payload
     * @throws Exception if token is invalid, expired, or cannot be parsed
     */
    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the given path is a public endpoint that should bypass authentication.
     * <p>
     * Public endpoints include authentication endpoints, actuator endpoints,
     * public resources, and the root path.
     *
     * @param path the request path to check
     * @return true if the path is public and should bypass authentication, false otherwise
     */
    private boolean isPublicEndpoint(String path) {
        return path.contains("/auth/") ||
                path.contains("/actuator/") ||
                path.contains("/public/") ||
                path.equals("/");
    }

    /**
     * Handles authentication errors by setting appropriate HTTP status.
     * <p>
     * Sets the response status code and completes the exchange without
     * forwarding the request to downstream services.
     *
     * @param exchange the server web exchange
     * @param err error message (currently not used, reserved for future logging)
     * @param httpStatus the HTTP status code to return (typically 401 Unauthorized)
     * @return Mono that completes the response with error status
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    /**
     * Configuration class for the JWT authentication filter.
     * <p>
     * This class is reserved for future configuration options such as:
     * <ul>
     *     <li>Configurable public endpoints</li>
     *     <li>Token validation options</li>
     *     <li>Custom claim extraction rules</li>
     * </ul>
     */
    public static class Config {
        // Configuration properties if needed
    }
}
