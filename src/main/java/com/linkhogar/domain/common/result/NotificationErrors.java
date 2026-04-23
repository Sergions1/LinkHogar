package com.linkhogar.domain.common.result;

import com.linkhogar.domain.common.result.Error;
import java.util.UUID;

public class NotificationErrors {
    private NotificationErrors() {}

    public static Error notFound(UUID notificationId) {
        return Error.notFound(
                "Notifications.NotFound",
                "La notificación con el Id = '" + notificationId + "' no fue encontrada"
        );
    }

    public static final Error NOT_FOUND_BY_USER = Error.notFound(
            "Notifications.NotFoundByUser",
            "No se encontraron notificaciones para el usuario especificado"
    );

    public static final Error ALREADY_READ = Error.conflict(
            "Notifications.AlreadyRead",
            "La notificación ya fue marcada como leída"
    );

    public static final Error UNAUTHORIZED = Error.unauthorized(
            "Notifications.Unauthorized",
            "No tienes permisos para acceder a esta notificación"
    );

    public static Error invalidType(String type) {
        return Error.validation(
                "Notifications.InvalidType",
                "El tipo de notificación '" + type + "' no es válido"
        );
    }

    public static Error NotEdited(String type) {
        return Error.validation(
                "Notifications.NotEdited",
                "No se pudo editar la notificación: " + type
        );
    }

    public static Error CreatingError(String type) {
        return Error.failure(
                "Notifications.NotEdited",
                "No se pudo crear la notificación: " + type
        );
    }
}
