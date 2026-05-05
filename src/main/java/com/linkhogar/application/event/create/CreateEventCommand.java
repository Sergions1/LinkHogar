package com.linkhogar.application.event.create;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventCommand(
        UUID homeId,
        UUID creatorId,
        String creatorName,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean allDay,
        int reminderMinutesBefore
) {}