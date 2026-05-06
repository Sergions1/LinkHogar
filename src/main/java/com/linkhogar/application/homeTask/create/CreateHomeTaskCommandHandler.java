package com.linkhogar.application.homeTask.create;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeTask;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import com.linkhogar.domain.homeTasks.enums.TaskStatus;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateHomeTaskCommandHandler {
    private final HomeTaskRepository homeTaskRepository;
    private final UserRepository userRepository;

    @Transactional
    public Result<UUID> handle(CreateHomeTaskCommand command) {
        try {
            Optional<User> userOpt = userRepository.userById(command.createdBy());

            if (userOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(command.createdBy()));
            }

            User user = userOpt.get();

            if (user.getHomeId() == null || !user.getHomeId().equals(command.homeId())) {
                return Result.failure(UserErrors.UNAUTHORIZED);
            }

            HomeTask task = new HomeTask();
            task.setTitle(command.title());
            task.setDescription(command.description());
            task.setStatus(TaskStatus.todo);
            task.setHomeId(command.homeId());
            task.setAssignedUserId(command.assignedUserId());
            task.setAssignedUserName(command.assignedUserName());
            task.setCreatedBy(command.createdBy());
            task.setCreatedByName(command.createdByName());
            task.setStartDate(command.startDate());
            task.setDueDate(command.dueDate());

            HomeTask savedTask = homeTaskRepository.save(task);

            return Result.success(savedTask.getId());
        } catch (Exception e) {
            System.out.println("Error al crear la tarea del hogar: " + e.getMessage());
            return Result.failure(Error.failure("500", "No se pudo crear la tarea"));
        }
    }
}
