package com.linkhogar.application.chat.initiateChat;

import java.util.UUID;

public record InitiateChatCommand(
        UUID houseId,
        UUID clientId,
        UUID ownerId,
        String initialMessage
) {}