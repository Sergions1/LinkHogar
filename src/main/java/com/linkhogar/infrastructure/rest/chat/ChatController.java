package com.linkhogar.infrastructure.rest.chat;

import com.linkhogar.application.chat.getByUser.ChatSummaryResponse;
import com.linkhogar.application.chat.getByUser.GetByUserQuery;
import com.linkhogar.application.chat.getByUser.GetByUserQueryHandle;
import com.linkhogar.application.chat.getMessagesByChat.GetMessagesByChatQueryHandler;
import com.linkhogar.application.chat.getMessagesByChat.MessageResponse;
import com.linkhogar.application.chat.initiateChat.InitiateChatCommand;
import com.linkhogar.application.chat.initiateChat.InitiateChatCommandHandler;
import com.linkhogar.application.chat.initiateChat.InitiateChatRequest;
import com.linkhogar.domain.chat.ChatParticipantRepository;
import com.linkhogar.domain.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.linkhogar.application.chat.getMessagesByChat.GetMessagesByChatQuery;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {
    private final ChatParticipantRepository chatParticipantRepository;
    private final InitiateChatCommandHandler initiateChatCommandHandler;
    private final GetByUserQueryHandle getByUserQueryHandle;
    private final GetMessagesByChatQueryHandler GetMessagesByChatQueryHandler;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateChat(@RequestBody InitiateChatRequest request, Authentication authentication) {

        // 1. Obtenemos el ID del usuario logueado de forma segura desde el token JWT
        // (Asumo que en tu configuración de seguridad el 'name' o el 'principal' guarda el ID)
        UUID clientId = UUID.fromString(authentication.getName());

        // 2. Montamos el comando
        InitiateChatCommand command = new InitiateChatCommand(
                request.houseId(),
                clientId,
                request.ownerId(),
                request.initialMessage()
        );

        // 3. Ejecutamos la lógica de negocio
        Result<UUID> result = initiateChatCommandHandler.handle(command);

        if (result.isSuccess()) {
            // Devolvemos un JSON con el ID del chat para que Angular redirija: { "chatId": "uuid-..." }
            return ResponseEntity.ok().body(new Object() {
                public final UUID chatId = result.getValue();
            });
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }


    }


    @GetMapping("/my-chats")
    public ResponseEntity<?> getMyChats(Authentication authentication) {
        // 1. Extraemos quién está pidiendo sus chats
        UUID userId = UUID.fromString(authentication.getName());

        // 2. Ejecutamos la consulta
        Result<List<ChatSummaryResponse>> result = getByUserQueryHandle.handle(new GetByUserQuery(userId));

        if(result.isSuccess()){
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            Authentication authentication) {

        try {
            UUID userId = UUID.fromString(authentication.getName());

            List<MessageResponse> messages = GetMessagesByChatQueryHandler.handle(new GetMessagesByChatQuery(chatId, userId, page, size));

            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
