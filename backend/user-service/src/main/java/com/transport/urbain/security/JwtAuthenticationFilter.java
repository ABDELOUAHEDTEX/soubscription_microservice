package com.transport.urbain.security;

import com.transport.urbain.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter for protecting endpoints.
 * 
 * <p>This filter intercepts all incoming requests and processes JWT tokens
 * from the Authorization header. It extracts the token, validates it, and
 * sets up the security context with the authenticated user.
 * 
 * <p>Filter behavior:
 * <ul>
 *   <li>Extracts JWT token from "Bearer ..." Authorization header</li>
 *   <li>Validates token and extracts user email</li>
 *   <li>Loads user details including roles</li>
 *   <li>Sets up authentication in SecurityContext</li>
 *   <li>Continues the filter chain</li>
 * </ul>
 * 
 * <p>If no token is present or token is invalid, the request continues
 * without authentication and will be handled by authorization checks.
 * 
 * @see OncePerRequestFilter
 * @see org.springframework.security.core.context.SecurityContextHolder
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Processes the JWT token from the request and sets up authentication if valid.
     * 
     * <p>This method:
     * <ol>
     *   <li>Extracts the JWT token from the Authorization header</li>
     *   <li>Validates the token and extracts the username</li>
     *   <li>Loads user details and authorities</li>
     *   <li>Creates and sets the authentication in SecurityContext</li>
     * </ol>
     * 
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain to continue
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
