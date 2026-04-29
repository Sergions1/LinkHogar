package com.linkhogar.domain.chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository {
    Chat save(Chat chat);
    Optional<Chat> findInquiryChatByHouseAndClient(UUID houseId, UUID clientId);
    List<Chat> findAllChatsByUserId(UUID userId);
    List<Chat> findByReferenceId(UUID houseId);
    Optional<Chat> findById(UUID chatId);
}
