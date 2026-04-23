package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT c FROM Chat c JOIN ChatParticipant cp ON c.id = cp.chatId " +
            "WHERE c.type = 'Alquiler' " +
            "AND c.referenceId = :houseId " +
            "AND cp.userId = :clientId")
    Optional<Chat> findInquiryChatByHouseAndClient(
            @Param("houseId") UUID houseId,
            @Param("clientId") UUID clientId
    );
}