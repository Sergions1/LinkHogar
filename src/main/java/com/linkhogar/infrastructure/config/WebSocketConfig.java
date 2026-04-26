package com.linkhogar.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Los prefijos de los canales a los que Angular se SUSCRIBIRÁ para recibir mensajes
        // Ej: client.subscribe('/topic/direct.123')
        config.enableSimpleBroker("/topic");

        // El prefijo de las rutas a las que Angular ENVIARÁ los mensajes
        // Ej: client.send('/app/chat.send', data)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // La URL inicial a la que Angular hace la conexión HTTP de "apretón de manos" para transformarla en un WebSocket.
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // Permite que Angular se conecte
                .withSockJS(); // Fallback para navegadores antiguos que no soporten WebSocket puros
    }
}
