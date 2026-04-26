package com.linkhogar.application.chat.deleteChat;

import com.linkhogar.domain.chat.Chat;
import com.linkhogar.domain.chat.ChatRepository;
import com.linkhogar.domain.chat.enums.ChatStatus;
import com.linkhogar.domain.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveHouseChatsCommandHandler {

    private final ChatRepository chatRepository;

    @Transactional
    public Result<Void> handle(ArchiveHouseChatsCommand command) {
        try {
            List<Chat> chats = chatRepository.findByReferenceId(command.houseId());

            for (Chat chat : chats) {
                chat.setStatus(ChatStatus.Archivado);
                chatRepository.save(chat);
            }

            return Result.success(null);
        }catch (Exception e){
            System.out.println("Error archivando chats de la casa: " + e.getMessage());
            return Result.failure(com.linkhogar.domain.common.result.Error.failure("500", "No se pudieron archivar los chats"));
        }
    }
}
