package com.linkhogar.application.homeTask.delete;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeTask;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import com.linkhogar.domain.homeTasks.HomeErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteCommandHandler {
    private final HomeTaskRepository homeTaskRepository;

    public Result<Void> handle(DeleteCommand command) {
        try {
            Optional<HomeTask> homeTask = homeTaskRepository.findById(command.taskId());

            if (homeTask.isEmpty()) {
                return Result.failure(HomeErrors.TaskNotFound(command.taskId()));
            }
            homeTaskRepository.deleteById(command.taskId());
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure(HomeErrors.TASK_UPDATE_FAILED);
        }
    }

}
