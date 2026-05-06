package com.linkhogar.application.chat.getOrCreateHomeChat;

import java.util.UUID;

public record GetOrCreateHomeChatCommand(
        UUID homeId,
        UUID requesterId
) {
}
