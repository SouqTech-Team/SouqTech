package org.stand.springbootecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "SouqTech API", version = "1.0.0", description = "Official documentation for the SouqTech e-commerce platform. "
                +
                "This API provides endpoints to manage products, categories, users, " +
                "orders, and customer reviews."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT Authentication. Enter 'Bearer ' followed by your token.")
public class OpenApiConfig {
}
