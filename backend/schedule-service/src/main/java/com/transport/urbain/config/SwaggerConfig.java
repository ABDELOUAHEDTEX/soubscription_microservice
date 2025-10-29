package com.transport.urbain.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the Schedule Service.
 * <p>
 * This configuration class sets up the API documentation for the Schedule Service,
 * including API metadata, contact information, license details, and security
 * configuration for JWT bearer token authentication.
 * <p>
 * The generated documentation will be accessible at /swagger-ui.html when the
 * application is running with springdoc-openapi enabled.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures the OpenAPI specification for the Schedule Service.
     * <p>
     * This bean creates the OpenAPI configuration with:
     * <ul>
     *     <li>API metadata: title, version, and description</li>
     *     <li>Contact information for support</li>
     *     <li>License information (Apache 2.0)</li>
     *     <li>JWT Bearer token authentication scheme</li>
     * </ul>
     * <p>
     * The configuration enables Swagger UI to display all API endpoints
     * and allows users to test endpoints with proper authentication.
     *
     * @return OpenAPI bean configured with API documentation details
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Configure API information
                .info(new Info()
                        .title("Schedule Service API")
                        .version("1.0.0")
                        .description("API documentation for Urban Transport Schedule Service")
                        .contact(new Contact()
                                .name("Transport Team")
                                .email("support@transport.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                // Add security requirement for all endpoints
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                // Configure JWT Bearer token authentication
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
