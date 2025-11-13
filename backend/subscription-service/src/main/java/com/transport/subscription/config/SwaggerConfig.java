package com.transport.subscription.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI subscriptionServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8085");
        devServer.setDescription("Serveur de développement");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.transport.urbain.com");
        prodServer.setDescription("Serveur de production");

        Contact contact = new Contact();
        contact.setEmail("support@transport.urbain.com");
        contact.setName("Équipe Support");
        contact.setUrl("https://transport.urbain.com/support");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Subscription Service API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST pour la gestion des abonnements au service de transport urbain. " +
                        "Cette API permet de gérer les plans d'abonnement, les abonnements utilisateurs, " +
                        "la facturation et les paiements.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}

