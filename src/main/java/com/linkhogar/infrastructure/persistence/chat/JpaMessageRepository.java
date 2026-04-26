package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByChatIdOrderByCreatedAtAsc(UUID chatId, Pageable pageable);
    Optional<Message> findFirstByChatIdOrderByCreatedAtDesc(UUID chatId);
}