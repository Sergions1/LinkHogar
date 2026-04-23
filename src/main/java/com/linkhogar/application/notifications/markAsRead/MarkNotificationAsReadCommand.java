package com.linkhogar.application.notifications.markAsRead;

import java.util.UUID;

public record MarkNotificationAsReadCommand (UUID notificationId,
                                             UUID userId){
}
