package com.linkhogar.application.chat.initiateChat;

import java.util.UUID;

public record InitiateChatRequest(
        UUID houseId,
        UUID ownerId,
        String initialMessage
) {}