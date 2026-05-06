package com.linkhogar.application.event.getByHome;

import java.time.LocalDateTime;
import java.util.UUID;

public record HomeEventResponse(
        UUID id,
        UUID creatorId,
        String creatorName,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean allDay,
        int reminderMinutesBefore
) {}
