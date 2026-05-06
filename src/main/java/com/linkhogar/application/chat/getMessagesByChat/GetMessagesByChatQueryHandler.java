package com.linkhogar.application.chat.getMessagesByChat;

import com.linkhogar.domain.chat.Chat;
import com.linkhogar.domain.chat.ChatParticipantRepository;
import com.linkhogar.domain.chat.ChatRepository;
import com.linkhogar.domain.chat.MessageRepository;
import com.linkhogar.domain.chat.enums.ChatType;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMessagesByChatQueryHandler {
    private final MessageRepository messageRepository;
    private final ChatParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<MessageResponse> handle(GetMessagesByChatQuery query) {

        // 1. Buscamos el chat y el usuario en la BD
        Chat chat = chatRepository.findById(query.chatId())
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        User user = userRepository.userById(query.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean hasAccess = false;

        // 2. LÓGICA DE ACCESO DINÁMICA
        if (chat.getType() == ChatType.GrupoHogar) {
            // Si es chat de casa: comprobamos que el usuario vive en esa casa
            if (user.getHomeId() != null && user.getHomeId().equals(chat.getReferenceId())) {
                hasAccess = true;
            }
        } else {
            // Si es un chat normal: miramos la tabla ChatParticipant
            hasAccess = participantRepository.findByChatId(query.chatId()).stream()
                    .anyMatch(p -> p.getUserId().equals(query.userId()));
        }

        // Si no pasa ninguno de los filtros, puerta (403)
        if (!hasAccess) {
            throw new RuntimeException("No tienes acceso a este chat");
        }

        Pageable pageable = PageRequest.of(query.page(), query.size());

        List<MessageResponse> messages = messageRepository.findByChatId(query.chatId(), pageable)
                .stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        query.chatId(),
                        msg.getSenderId(),
                        msg.getContent(),
                        msg.getCreatedAt(),
                        msg.getSenderName()
                ))
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }
}
