package com.linkhogar.application.chat.initiateChat;


import com.linkhogar.application.notifications.createNotification.CreateNotificationCommand;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommandHandler;
import com.linkhogar.domain.chat.*;
import com.linkhogar.domain.chat.enums.ChatType;
import com.linkhogar.domain.common.result.Result; // Ajusta a tu ruta real de Result
import com.linkhogar.domain.common.result.Error; // Ajusta a tu ruta real de Error si lo usas
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitiateChatCommandHandler {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final CreateNotificationCommandHandler notificationHandler;

    @Transactional
    public Result<UUID> handle(InitiateChatCommand command) {
        try {
            Optional<Chat> existingChat = chatRepository.findInquiryChatByHouseAndClient(command.houseId(), command.clientId());

            Chat chat;
            if (existingChat.isEmpty()) {
                chat = Chat.builder()
                        .id(UUID.randomUUID())
                        .type(ChatType.Alquiler)
                        .referenceId(command.houseId())
                        .createdAt(LocalDateTime.now())
                        .build();
                chatRepository.save(chat);

                ChatParticipant client = ChatParticipant.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(command.clientId())
                        .joinedAt(LocalDateTime.now())
                        .lastReadAt(LocalDateTime.now()) // El cliente lo lee instantáneamente al enviarlo
                        .build();

                ChatParticipant owner = ChatParticipant.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(command.ownerId())
                        .joinedAt(LocalDateTime.now())
                        .lastReadAt(LocalDateTime.now().minusDays(1)) // Fecha pasada = El dueño tiene mensajes sin leer
                        .build();

                participantRepository.saveAll(List.of(client, owner));
            } else {
                chat = existingChat.get();
                // Opcional: Podrías actualizar el lastReadAt del cliente aquí para asegurar que consta como leído
            }

            Message message = Message.builder()
                    .id(UUID.randomUUID())
                    .chatId(chat.getId())
                    .senderId(command.clientId())
                    .content(command.initialMessage())
                    .createdAt(LocalDateTime.now())
                    .build();
            messageRepository.save(message);

            // 3. NOTIFICAMOS AL DUEÑO
            notificationHandler.handle(new CreateNotificationCommand(
                    command.ownerId(),
                    "Nuevo mensaje",
                    "Tienes un nuevo interesado en tu anuncio."
            ));

            // Devolvemos el ID del chat para que Angular pueda redirigir a /mensajes/{chatId}
            return Result.success(chat.getId());

        } catch (Exception e) {
            System.out.println("Error al iniciar el chat: " + e.getMessage());
            // Ajusta el retorno de Error según la estructura de tu proyecto
            return Result.failure(Error.failure("500", "No se pudo iniciar el chat"));
        }
    }
}