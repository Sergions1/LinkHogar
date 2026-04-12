package com.linkhogar.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // 1. CORS: Activamos la configuración por defecto (busca el Bean de abajo)
                .cors(Customizer.withDefaults())
                // 1. Desactivar CSRF (No es necesario en APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Gestionar permisos de rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .requestMatchers( HttpMethod.GET,"/verify/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/currentUser").authenticated()
                        .requestMatchers(HttpMethod.GET, "/admin/stats").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.PUT, "/users/update/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users/addFavourite/*/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/favourites/ids/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/updateAvatar/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/change-password").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/*").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.PATCH, "/users/*/toggle-enabled").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.POST, "/admin/create-user").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.GET, "/admin/pendind").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.GET, "/houses/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/houses/*/status").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.DELETE, "/houses/*").hasAnyAuthority("Admin", "LinkHogar")
                        .requestMatchers(HttpMethod.GET, "/AppSettings/*").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir solo tu frontend (Ojo: sin barra al final)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",         // Para tus pruebas en local
                "https://linkhogar.com",         // Tu dominio principal
                "https://www.linkhogar.com",     // Variante con www
                "https://api.linkhogar.com"     // Servidor
        ));

        // Permitir todos los verbos HTTP (GET, POST, PUT, DELETE...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permitir cabeceras (Tokens, Content-Type...)
        configuration.setAllowedHeaders(List.of("*"));

        // Permitir enviar cookies o credenciales
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
