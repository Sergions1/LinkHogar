package com.linkhogar.domain.chat;

import java.util.List;
import java.util.UUID;

public interface ChatParticipantRepository {
    List<ChatParticipant> saveAll(List<ChatParticipant> participants);
    List<ChatParticipant> findByChatId(UUID chatId);

}