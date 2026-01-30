package com.linkhogar.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "LinkHogar API",
                version = "1.0.0",
                description = "Documentación."
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth") //Aplicamos la seguridad a toda la API
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Autenticación JWT. Copia el token del endpoint /auth/login y pégalo aquí.",
        scheme = "bearer", //Se añade al principio del token pegado
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
