package com.linkhogar.application.event.update;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateEventCommand(
        UUID eventId,
        UUID homeId, // Lo pedimos por seguridad, para que nadie edite eventos de otra casa
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean allDay,
        int reminderMinutesBefore
) {}