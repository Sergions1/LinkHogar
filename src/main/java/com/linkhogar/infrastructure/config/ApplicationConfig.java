package com.linkhogar.infrastructure.config;

import com.linkhogar.application.settings.getByKey.GetAppSettingsByKeyQuery;
import com.linkhogar.application.settings.getByKey.GetAppSettingsByKeyQueryHandler;
import com.linkhogar.application.settings.updateAppSetting.UpdateAppSettingCommand;
import com.linkhogar.application.settings.updateAppSetting.UpdateAppSettingCommandHandler;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final GetAppSettingsByKeyQueryHandler getQueryHandler;
    private final UpdateAppSettingCommandHandler updateCommandHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new Pbkdf2PasswordEncoder(
                "", //Salt vacio debida a que Spring ya genera uno aleatorio
                32,
                185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512
        );
    }

    @Bean
    public UserDetailsService userDetailsService(){
        //Recibe el username (id extraido del token) y busca en base de datos.
        return username -> userRepository.userById(UUID.fromString(username)).orElseThrow(() -> new UsernameNotFoundException(("Usuario no encontrado")));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Nominatim requiere un User-Agent
        return new RestTemplate(factory) {{
            getInterceptors().add((request, body, execution) -> {
                request.getHeaders().set("User-Agent", "LinkHogar/1.0");
                return execution.execute(request, body);
            });
        }};
    }

    @Bean
    public CommandLineRunner initDefaultSettings() {
        return args -> {
            String heroImageName = "HERO_INITIAL_IMAGE";
            // Sustituye por la URL real de Cloudinary que quieras mostrar por defecto
            String defaultHeroImageUrl = "https://res.cloudinary.com/dnhoytwpu/image/upload/v1774483515/fotoIndex_udtvaf.png";

            GetAppSettingsByKeyQuery query = new GetAppSettingsByKeyQuery(heroImageName, null);
            String currentSetting = getQueryHandler.handle(query);

            if (currentSetting == null) {
                UpdateAppSettingCommand command = new UpdateAppSettingCommand(
                        heroImageName,
                        defaultHeroImageUrl,
                        "Imagen inicial del buscador en la página principal (Explore)"
                );
                updateCommandHandler.handle(command);
            }
        };
    }
}
