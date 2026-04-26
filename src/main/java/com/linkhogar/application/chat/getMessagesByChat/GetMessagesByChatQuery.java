package com.linkhogar.application.chat.getMessagesByChat;

import java.util.UUID;

public record GetMessagesByChatQuery(
        UUID chatId,
        UUID userId,
        int page,
        int size
) {
}
