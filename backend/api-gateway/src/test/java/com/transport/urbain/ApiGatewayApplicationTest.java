package com.transport.urbain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link ApiGatewayApplication}.
 * <p>
 * Tests verify basic application structure and existence of main method.
 */
@DisplayName("API Gateway Application Tests")
class ApiGatewayApplicationTest {

    @Test
    @DisplayName("Should create ApiGatewayApplication instance")
    void testApplicationCreation() {
        // Given & When
        ApiGatewayApplication application = new ApiGatewayApplication();

        // Then
        assertNotNull(application, "Application instance should be created");
    }

    @Test
    @DisplayName("Should have main method")
    void testMainMethodExists() throws NoSuchMethodException {
        // Given
        Class<?> appClass = ApiGatewayApplication.class;

        // When
        var mainMethod = appClass.getDeclaredMethod("main", String[].class);

        // Then
        assertNotNull(mainMethod, "Main method should exist");
    }
}

