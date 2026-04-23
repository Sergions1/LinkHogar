package com.linkhogar.application.notifications.getUnread;

import com.linkhogar.domain.common.Notification;
import com.linkhogar.domain.common.NotificationRepository;
import com.linkhogar.domain.common.result.NotificationErrors;
import com.linkhogar.domain.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUnreadNotificationsQueryHandler {
    private final NotificationRepository notificationRepository;

    public Result<List<Notification>> handle(GetUnreadNotificationsQuery query) {
        try{
            List<Notification> notifications = notificationRepository.getUnreadNotifications(query.userId());
            return Result.success(notifications);
        }catch (Exception e){
            return Result.failure(NotificationErrors.NOT_FOUND_BY_USER);
        }
    }
}
