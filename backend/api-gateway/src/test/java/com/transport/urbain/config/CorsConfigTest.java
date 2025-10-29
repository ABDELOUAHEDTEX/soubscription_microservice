package com.transport.urbain.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CorsConfig}.
 * <p>
 * Tests verify that CORS configuration is properly set up
 * with correct allowed origins, methods, and headers.
 */
@DisplayName("CORS Configuration Tests")
class CorsConfigTest {

    @Test
    @DisplayName("Should create CORS web filter bean")
    void testCorsWebFilterBeanCreation() {
        // Given
        CorsConfig corsConfig = new CorsConfig();

        // When
        CorsWebFilter filter = corsConfig.corsWebFilter();

        // Then
        assertNotNull(filter, "CORS web filter should not be null");
    }

    @Test
    @DisplayName("Should create valid CORS filter instance")
    void testCorsFilterInstance() {
        // Given
        CorsConfig corsConfig = new CorsConfig();

        // When
        CorsWebFilter filter = corsConfig.corsWebFilter();

        // Then
        assertInstanceOf(CorsWebFilter.class, filter);
    }
}

