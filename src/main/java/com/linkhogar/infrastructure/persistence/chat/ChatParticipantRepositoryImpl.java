package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.ChatParticipant;
import com.linkhogar.domain.chat.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepository {

    private final JpaChatParticipantRepository jpaChatParticipantRepository;

    @Override
    public List<ChatParticipant> saveAll(List<ChatParticipant> participants) {
        return jpaChatParticipantRepository.saveAll(participants);
    }

    @Override
    public List<ChatParticipant> findByChatId(UUID chatId) {
        return jpaChatParticipantRepository.findByChatId(chatId);
    }
}