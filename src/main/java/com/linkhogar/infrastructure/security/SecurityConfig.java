package com.linkhogar.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // 1. Desactivamos CSRF (Cross-Site Request Forgery)
        // Esto es necesario para que Postman pueda enviar POSTs sin tokens especiales.
        // En APIs REST modernas sin estado (stateless), suele desactivarse.
        return http.csrf(AbstractHttpConfigurer::disable)

        // 2. Configuramos las reglas de quién entra y quién no
        .authorizeHttpRequests(auth -> auth
                // A. PERMITIMOS EL REGISTRO:
                // "Si alguien intenta hacer POST a /users, déjalo pasar sin preguntar"
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                // B. BLOQUEAMOS EL RESTO:
                // "Cualquier otra petición requiere que el usuario esté autenticado"
                .anyRequest().authenticated()
        )
        .build();
    }
}
