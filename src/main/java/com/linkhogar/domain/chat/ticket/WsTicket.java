package com.linkhogar.domain.chat.ticket;

import java.util.UUID;

public record WsTicket(
        String id,
        UUID userId,
        long expiryTime
) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}