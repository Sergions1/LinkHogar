package com.linkhogar.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // 1. Desactivar CSRF (No es necesario en APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Gestionar permisos de rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()

                        .anyRequest().authenticated()
                )
                // 3. Gestión de Sesión: STATELESS
                // No queremos que el servidor guarde sesiones. Cada petición debe llevar su Token.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider) //Añadimos el proveedor
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //Añadir el filtro antes del filtro estándar

        return http.build();
    }
}
