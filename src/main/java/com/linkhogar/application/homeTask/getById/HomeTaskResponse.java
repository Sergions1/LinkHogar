package com.linkhogar.application.homeTask.getById;

import java.time.LocalDateTime;
import java.util.UUID;

public record HomeTaskResponse(
        UUID id,
        String title,
        String description,
        String status,
        UUID assignedUserId,
        String assignedUserName,
        String createdByName,
        LocalDateTime startDate,
        LocalDateTime dueDate,
        LocalDateTime completedAt
) {}
