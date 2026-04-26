package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Chat;
import com.linkhogar.domain.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final JpaChatRepository jpaChatRepository;

    @Override
    public Chat save(Chat chat) {
        return jpaChatRepository.save(chat);
    }

    @Override
    public Optional<Chat> findInquiryChatByHouseAndClient(UUID houseId, UUID clientId) {
        return jpaChatRepository.findInquiryChatByHouseAndClient(houseId, clientId);
    }

    @Override
    public List<Chat> findAllChatsByUserId(UUID userId) {
        return jpaChatRepository.findAllChatsByUserId(userId);
    }

    @Override
    public List<Chat> findByReferenceId(UUID referenceId) {
        return jpaChatRepository.findByReferenceId(referenceId);
    }


}