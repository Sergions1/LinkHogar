package com.linkhogar.application.homeTask.create;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateHomeTaskCommand (
        String title,
        String description,
        UUID homeId,
        UUID assignedUserId,
        String assignedUserName,
        UUID createdBy,
        String createdByName,
        LocalDateTime startDate,
        LocalDateTime dueDate
){}
