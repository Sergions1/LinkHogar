package com.linkhogar.application.chat.getMessagesByChat;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse (UUID id,
                               UUID chatId,
                               UUID senderId,
                               String content,
                               LocalDateTime createdAt) {

}
