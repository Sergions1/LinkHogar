package com.linkhogar.application.user.delete;

import com.linkhogar.domain.chat.Chat;
import com.linkhogar.domain.chat.ChatParticipant;
import com.linkhogar.domain.chat.ChatParticipantRepository;
import com.linkhogar.domain.chat.ChatRepository;
import com.linkhogar.domain.chat.enums.ChatStatus;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserCommandHandler {
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRepository chatRepository;

    public Result<Void> handle(DeleteUserCommand command){
        if(userRepository.userById(command.userId()).isEmpty()){
            return Result.failure(UserErrors.NotFound(command.userId()));
        }

        List<ChatParticipant> participations = chatParticipantRepository.findByUserId(command.userId());

        for (ChatParticipant participation : participations) {
            UUID chatId = participation.getChatId(); // O participation.getChat().getId() según cómo lo tengas mapeado

            // 2. Contamos cuánta gente hay AHORA MISMO en ese chat
            long participantCount = chatParticipantRepository.countByChatId(chatId);

            // 3. Si solo queda 1 persona (el usuario que estamos a punto de borrar) o menos...
            if (participantCount <= 1) {
                Chat chat = chatRepository.findById(chatId).orElse(null);
                if (chat != null) {
                    chat.setStatus(ChatStatus.Archivado);
                    chatRepository.save(chat);
                }
            }
        }

        userRepository.delete(command.userId());

        return Result.success(null);

    }
}
