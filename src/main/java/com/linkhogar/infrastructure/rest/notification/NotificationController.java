package com.linkhogar.infrastructure.rest.notification;

import com.linkhogar.application.notifications.getUnread.GetUnreadNotificationsQuery;
import com.linkhogar.application.notifications.getUnread.GetUnreadNotificationsQueryHandler;
import com.linkhogar.application.notifications.markAsRead.MarkNotificationAsReadCommand;
import com.linkhogar.application.notifications.markAsRead.MarkNotificationAsReadCommandHandler;
import com.linkhogar.domain.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetUnreadNotificationsQueryHandler getUnreadHandler;
    private final MarkNotificationAsReadCommandHandler markAsReadHandler;

    @GetMapping("/unread")
    public ResponseEntity<?> getUnread(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID userId = UUID.fromString(authentication.getName());
        Result<?> result = getUnreadHandler.handle(new GetUnreadNotificationsQuery(userId));

        return result.isSuccess() ? ResponseEntity.ok(result.getValue()) : ResponseEntity.badRequest().body(result.getError());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID userId = UUID.fromString(authentication.getName());
        Result<Void> result = markAsReadHandler.handle(new MarkNotificationAsReadCommand(id, userId));

        return result.isSuccess() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(result.getError());
    }
}