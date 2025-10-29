package com.transport.urbain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom authentication entry point for unauthorized access.
 * 
 * <p>This component handles authentication failures by returning a standardized
 * JSON error response instead of the default Spring Security behavior.
 * 
 * <p>When an unauthenticated user attempts to access a protected resource,
 * this entry point is invoked and returns:
 * <ul>
 *   <li>HTTP 401 Unauthorized status</li>
 *   <li>JSON error response with timestamp, status, error, message, and path</li>
 *   <li>Detailed error information for client handling</li>
 * </ul>
 * 
 * <p>Error response format:
 * <pre>
 * {
 *   "timestamp": "2024-01-01T12:00:00",
 *   "status": 401,
 *   "error": "Unauthorized",
 *   "message": "Authentication error message",
 *   "path": "/api/users"
 * }
 * </pre>
 * 
 * @see org.springframework.security.web.AuthenticationEntryPoint
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences the authentication entry point when unauthorized access occurs.
     * 
     * <p>Returns a JSON error response with 401 status code and detailed
     * information about the authentication failure.
     * 
     * @param request the HTTP request that resulted in an AuthenticationException
     * @param response the HTTP response to write the error to
     * @param authException the authentication exception that was thrown
     * @throws IOException if an I/O error occurs writing the response
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
