package com.linkhogar.application.homeTask.create;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateHomeTaskRequest (
        String title,
        String description,
        UUID homeId,
        UUID assignedUserId,
        String assignedUserName,
        String createdByName, // Se pide al frontend temporalmente o se saca de un UserService
        LocalDateTime startDate,
        LocalDateTime dueDate
) {}
