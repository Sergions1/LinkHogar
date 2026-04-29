package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {
    List<ChatParticipant> findByChatId(UUID chatId);
    List<ChatParticipant> findByUserId(UUID userId);
    long countByChatId(UUID chatId);


}