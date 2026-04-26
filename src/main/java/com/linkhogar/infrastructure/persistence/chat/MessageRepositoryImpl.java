package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Message;
import com.linkhogar.domain.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final JpaMessageRepository jpaMessageRepository;

    @Override
    public Message save(Message message) {
        return jpaMessageRepository.save(message);
    }

    @Override
    public Page<Message> findByChatId(UUID chatId, Pageable pageable) {
        return jpaMessageRepository.findByChatIdOrderByCreatedAtAsc(chatId, pageable);
    }

    @Override
    public Optional<Message> findFirstByChatIdOrderByCreatedAtDesc(UUID chatId) {
        return jpaMessageRepository.findFirstByChatIdOrderByCreatedAtDesc(chatId);
    }
}