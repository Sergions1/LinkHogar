package com.linkhogar.application.event.update;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateEventRequest(
        UUID homeId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean allDay,
        int reminderMinutesBefore
) {}