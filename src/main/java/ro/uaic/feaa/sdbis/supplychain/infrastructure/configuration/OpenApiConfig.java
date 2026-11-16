package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurare OpenAPI 3.0 / Swagger UI cu suport integrat pentru autentificare JWT Bearer.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Supply Chain & Vendor Management (Procure-to-Pay) API")
                        .version("1.0.0")
                        .description("REST API Enterprise conform cerintelor Master SDBIS: Achizitii, Receptie marfa, Control Calitate AQL, 3-Way Matching, Plati Stripe Sandbox si DMS.")
                        .contact(new Contact().name("Echipa SupplyChain P2P").email("contact@supplychain.ro"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introduceti token-ul JWT obtinut de la /api/auth/login sub formatul: Bearer <token>")));
    }
}
