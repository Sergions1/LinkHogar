package com.linkhogar.application.chat.sendMessage;


import java.util.UUID;

public record SendMessageCommand(
        UUID chatId,
        UUID senderId,
        String content
) {}