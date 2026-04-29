package com.linkhogar.application.chat.getByUser;

import com.linkhogar.domain.chat.*;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetByUserQueryHandle {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    @Transactional(readOnly = true)
    public Result<List<ChatSummaryResponse>> handle(GetByUserQuery query) {
        List<ChatSummaryResponse> responses = new ArrayList<>();

        // 1. Obtener todos los chats del usuario
        List<Chat> userChats = chatRepository.findAllChatsByUserId(query.userId());

        for (Chat chat : userChats) {
            // 2. Obtener participantes y separar quién soy "yo" y quién es el "otro"
            List<ChatParticipant> participants = participantRepository.findByChatId(chat.getId());

            ChatParticipant myParticipant = null;
            UUID otherUserId = query.userId(); // Fallback

            for (ChatParticipant p : participants) {
                if (p.getUserId().equals(query.userId())) {
                    myParticipant = p;
                } else {
                    otherUserId = p.getUserId();
                }
            }

            // Nombre del otro usuario
            User user = userRepository.userById(otherUserId).orElse(null);
            String otherUserName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "Usuario Eliminado";

            // 3. Buscar datos de la casa
            House house = houseRepository.getById(chat.getReferenceId());
            String houseTitle = house.getTitle();
            String houseImage = house.getImages().isEmpty() ? null : house.getImages().getFirst();

            // 4. Buscar el último mensaje REAL
            Optional<Message> lastMsgOpt = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId());

            String lastMessageText = "";
            LocalDateTime lastMessageTime = chat.getCreatedAt();
            boolean hasUnread = false;

            if (lastMsgOpt.isPresent()) {
                Message lastMsg = lastMsgOpt.get();
                lastMessageText = lastMsg.getContent();
                lastMessageTime = lastMsg.getCreatedAt();

                // Lógica del punto rojo:
                // Si NO lo envié yo, comprobamos si es más nuevo que mi última lectura
                if (!lastMsg.getSenderId().equals(query.userId())) {
                    LocalDateTime myLastRead = myParticipant != null ? myParticipant.getLastReadAt() : null;

                    // Si nunca he leído el chat, o el mensaje es posterior a mi última lectura -> No leído
                    if (myLastRead == null || lastMsg.getCreatedAt().isAfter(myLastRead)) {
                        hasUnread = true;
                    }
                }
            }

            // 5. Armar la respuesta
            responses.add(new ChatSummaryResponse(
                    chat.getId(),
                    otherUserName,
                    houseTitle,
                    houseImage,
                    lastMessageText,
                    lastMessageTime,
                    hasUnread
            ));
        }

        return Result.success(responses);
    }
}