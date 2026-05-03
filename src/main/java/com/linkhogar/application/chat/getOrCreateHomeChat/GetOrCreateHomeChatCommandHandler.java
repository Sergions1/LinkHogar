package com.linkhogar.application.chat.getOrCreateHomeChat;

import com.linkhogar.domain.chat.Chat;
import com.linkhogar.domain.chat.ChatRepository;
import com.linkhogar.domain.chat.enums.ChatStatus;
import com.linkhogar.domain.chat.enums.ChatType;
import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeErrors;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrCreateHomeChatCommandHandler {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public Result<UUID> handle(GetOrCreateHomeChatCommand command) {
        try {
            // 1. Seguridad: Verificar que el usuario pertenece al hogar
            Optional<User> userOpt = userRepository.userById(command.requesterId());
            if (userOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(command.requesterId()));
            }

            User user = userOpt.get();
            if (user.getHomeId() == null || !user.getHomeId().equals(command.homeId())) {
                return Result.failure(HomeErrors.UNAUTHORIZED_ACCESS);
            }

            // 2. Buscar si ya existe el chat del hogar
            List<Chat> chats = chatRepository.findByReferenceId(command.homeId());
            Optional<Chat> homeChat = chats.stream()
                    .filter(c -> c.getType() == ChatType.GrupoHogar)
                    .findFirst();

            if (homeChat.isPresent()) {
                return Result.success(homeChat.get().getId());
            }

            // 3. Si no existe, lo creamos
            Chat newChat = Chat.builder()
                    .id(UUID.randomUUID())
                    .type(ChatType.GrupoHogar)
                    .referenceId(command.homeId())
                    .createdAt(LocalDateTime.now())
                    .status(ChatStatus.Activo)
                    .build();

            chatRepository.save(newChat);

            return Result.success(newChat.getId());

        } catch (Exception e) {
            System.out.println("Error al obtener/crear el chat del hogar: " + e.getMessage());
            return Result.failure(HomeErrors.GET_CHAT_FAILED);
        }
    }
}