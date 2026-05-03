package com.linkhogar.application.chat.sendMessage;

import com.linkhogar.application.chat.getMessagesByChat.MessageResponse;
import com.linkhogar.domain.chat.Message;
import com.linkhogar.domain.chat.MessageRepository;
import com.linkhogar.domain.chat.ChatParticipant;
import com.linkhogar.domain.chat.ChatParticipantRepository;
import com.linkhogar.domain.common.Notification;
import com.linkhogar.domain.common.NotificationRepository;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendMessageCommandHandler {

    private final MessageRepository messageRepository;
    private final ChatParticipantRepository participantRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse handle(SendMessageCommand command) {

        String senderName = userRepository.userById(command.senderId())
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .orElse("Usuario desconocido");

        // 1. Guardar el mensaje del chat en la base de datos
        Message savedMessage = Message.builder()
                .id(UUID.randomUUID())
                .chatId(command.chatId())
                .senderId(command.senderId())
                .content(command.content())
                .createdAt(LocalDateTime.now())
                .senderName(senderName)
                .build();

        messageRepository.save(savedMessage);

        // 2. LÓGICA DE NOTIFICACIONES: Buscar al receptor y avisarle
        participantRepository.findByChatId(command.chatId()).stream()
                .map(ChatParticipant::getUserId)
                .filter(userId -> !userId.equals(command.senderId())) // Filtramos para no notificarnos a nosotros mismos
                .findFirst()
                .ifPresent(receiverId -> {
                    // Guardar la notificación en la BD
                    Notification notif = Notification.builder()
                            .userId(receiverId)
                            .title("Nuevo mensaje")
                            .message("Tienes un nuevo mensaje sin leer en el chat.")
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build();

                    notificationRepository.save(notif);

                    // Disparar la notificación al canal personal del receptor
                    messagingTemplate.convertAndSend("/topic/user." + receiverId, notif);
                });

        // 3. Devolver la respuesta formateada para que el Controlador la mande a la sala de chat
        return new MessageResponse(
                savedMessage.getId(),
                command.chatId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt(),
                savedMessage.getSenderName()
        );
    }
}
