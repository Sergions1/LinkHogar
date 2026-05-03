package com.linkhogar.application.homeTask.updateStatus;

import com.linkhogar.domain.homeTasks.enums.TaskStatus;

import java.util.UUID;

public record UpdateStatusCommand (
    UUID taskId,
    TaskStatus status,
    UUID requesterId
){
}
