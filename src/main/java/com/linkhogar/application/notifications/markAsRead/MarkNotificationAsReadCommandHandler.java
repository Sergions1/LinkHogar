package com.linkhogar.application.notifications.markAsRead;


import com.linkhogar.domain.common.Notification;
import com.linkhogar.domain.common.NotificationRepository;
import com.linkhogar.domain.common.result.NotificationErrors;
import com.linkhogar.domain.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarkNotificationAsReadCommandHandler {

    // Necesitaremos añadir un método getById en tu NotificationRepository (el puerto)
    // Asegúrate de añadir: Optional<Notification> getById(UUID id); en tu interfaz de dominio
    // y su implementación llamando a jpaRepository.findById(id).
    private final NotificationRepository notificationRepository;

    public Result<Void> handle(MarkNotificationAsReadCommand command) {
        try {
            Optional<Notification> notifOpt = notificationRepository.getById(command.notificationId());

            if (notifOpt.isEmpty()) {
                return Result.failure(NotificationErrors.notFound(command.notificationId()));
            }

            Notification notification = notifOpt.get();

            // Seguridad: Comprobamos que la notificación pertenece al usuario del token
            if (!notification.getUserId().equals(command.userId())) {
                return Result.failure(NotificationErrors.NOT_FOUND_BY_USER);
            }

            notification.setRead(true);
            notificationRepository.save(notification);

            return Result.success(null);

        } catch (Exception e) {
            return Result.failure(NotificationErrors.NotEdited(e.getMessage()));
        }
    }
}
