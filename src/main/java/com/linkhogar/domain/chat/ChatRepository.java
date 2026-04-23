package com.linkhogar.domain.chat;

import java.util.Optional;
import java.util.UUID;

public interface ChatRepository {
    Chat save(Chat chat);
    Optional<Chat> findInquiryChatByHouseAndClient(UUID houseId, UUID clientId);
}
