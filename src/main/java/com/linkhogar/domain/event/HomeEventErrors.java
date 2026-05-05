package com.linkhogar.domain.event;

import com.linkhogar.domain.common.result.Error;

public class HomeEventErrors {
    public static Error NotFound(java.util.UUID eventId) {
        return Error.notFound(
                "Events.NotFound",
                "El evento con el Id = '" + eventId + "' no fue encontrado"
        );
    }

    public static final Error UNAUTHORIZED_ACCESS = Error.unauthorized(
            "Events.UnauthorizedAccess",
            "Acceso denegado: No tienes permisos sobre este evento"
    );

    public static final Error INVALID_DATES = Error.validation(
            "Events.InvalidDates",
            "La fecha de finalización no puede ser anterior a la fecha de inicio"
    );

    public static final Error CREATION_FAILED = Error.failure(
            "Events.CreationFailed",
            "No se pudo registrar el evento"
    );

    public static final Error DELETION_FAILED = Error.failure(
            "Events.DeletionFailed",
            "No se pudo eliminar el evento"
    );

    public static final Error TTITLE_REQUIRED = Error.failure(
            "Events.TitleRequired",
            "El título del evento es obligatorio"
    );
}
