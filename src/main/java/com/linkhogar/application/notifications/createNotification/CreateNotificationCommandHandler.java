package com.linkhogar.application.notifications.createNotification;

import com.linkhogar.domain.common.Notification;
import com.linkhogar.domain.common.NotificationRepository;
import com.linkhogar.domain.common.result.NotificationErrors;
import com.linkhogar.domain.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateNotificationCommandHandler {

    private final NotificationRepository notificationRepository;

    public Result<Void> handle(CreateNotificationCommand command) {
        try {
            Notification notification = Notification.builder()
                    .userId(command.userId())
                    .title(command.title())
                    .message(command.message())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            return Result.success(null);

        } catch (Exception e) {
            return Result.failure(NotificationErrors.CreatingError(e.getMessage()));
        }
    }
}