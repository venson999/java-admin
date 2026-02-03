package com.java.admin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for SpringDoc integration.
 * Configures API documentation metadata and JWT authentication scheme.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_JWT = "bearer-jwt";

    /**
     * Configure OpenAPI bean with API metadata and JWT security scheme.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java Admin System API")
                        .description("Backend management system API documentation")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(BEARER_JWT))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("access_token")));
    }
}
