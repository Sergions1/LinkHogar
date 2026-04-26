package com.linkhogar.application.chat.getByUser;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSummaryResponse(
    UUID id,
    String otherParticipantName,
    String houseTitle,
    String houseImage,
    String lastMessage,
    LocalDateTime lastMessageTime,
    boolean hasUnread
) {}
