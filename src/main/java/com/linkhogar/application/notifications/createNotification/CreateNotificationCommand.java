package com.linkhogar.application.notifications.createNotification;

import java.util.UUID;

public record CreateNotificationCommand(
        UUID userId,
        String title,
        String message
) {}
