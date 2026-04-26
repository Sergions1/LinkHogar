package com.linkhogar.infrastructure.rest.chat;

import com.linkhogar.application.chat.getMessagesByChat.MessageResponse;
import com.linkhogar.application.chat.sendMessage.SendMessageCommand;
import com.linkhogar.application.chat.sendMessage.SendMessageCommandHandler;
import com.linkhogar.domain.chat.Message;
import com.linkhogar.domain.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller // 👈 Ojo: @Controller, no @RestController
@RequiredArgsConstructor
public class ChatMessageController {

    // Esta es la herramienta mágica que envía mensajes a los canales (topics)
    private final SimpMessagingTemplate messagingTemplate;
    private final SendMessageCommandHandler sendMessageCommandHandler;


    public record IncomingMessageDTO(UUID senderId, String content) {}

    @MessageMapping("/chat/{chatId}/sendMessage")
    public void sendMessage(@DestinationVariable UUID chatId, IncomingMessageDTO incomingMessage) {

        // 1. Convertir el DTO de entrada al Command de Aplicación
        SendMessageCommand command = new SendMessageCommand(
                chatId,
                incomingMessage.senderId(),
                incomingMessage.content()
        );

        // 2. Ejecutar el caso de uso
        MessageResponse response = sendMessageCommandHandler.handle(command);

        // 3. Emitir el mensaje al canal de la sala de chat
        messagingTemplate.convertAndSend("/topic/chat." + chatId, response);
    }
}