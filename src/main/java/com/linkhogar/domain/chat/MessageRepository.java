package com.linkhogar.domain.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Page<Message> findByChatId(UUID chatId, Pageable pageable);
    Optional<Message> findFirstByChatIdOrderByCreatedAtDesc(UUID chatId);
}
