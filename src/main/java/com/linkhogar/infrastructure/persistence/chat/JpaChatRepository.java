package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT c FROM Chat c JOIN ChatParticipant cp ON c.id = cp.chatId " +
            "WHERE c.type = com.linkhogar.domain.chat.enums.ChatType.Alquiler " +
            "AND c.referenceId = :houseId " +
            "AND cp.userId = :clientId " +
            "AND c.status = com.linkhogar.domain.chat.enums.ChatStatus.Activo")
    Optional<Chat> findInquiryChatByHouseAndClient(
            @Param("houseId") UUID houseId,
            @Param("clientId") UUID clientId
    );

    @Query("SELECT c FROM Chat c JOIN ChatParticipant cp ON c.id = cp.chatId WHERE cp.userId = :userId AND c.status = com.linkhogar.domain.chat.enums.ChatStatus.Activo")
    List<Chat> findAllChatsByUserId(@Param("userId") UUID userId);

    List<Chat> findByReferenceId(UUID referenceId);
    Optional<Chat> findById(UUID chatId);

}