package com.linkhogar.application.homeTask.updateStatus;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeTask;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import com.linkhogar.domain.homeTasks.HomeErrors;
import com.linkhogar.domain.homeTasks.enums.TaskStatus;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateStatusCommandHandler {
    private final HomeTaskRepository homeTaskRepository;
    private final UserRepository userRepository;

    public Result<Void> handler(UpdateStatusCommand command){
        try {
            // 1. Buscar usuario y tarea
            Optional<User> userOpt = userRepository.userById(command.requesterId());
            if (userOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(command.requesterId()));
            }

            Optional<HomeTask> taskOpt = homeTaskRepository.findById(command.taskId());
            if (taskOpt.isEmpty()) {
                return Result.failure(HomeErrors.TaskNotFound(command.taskId()));
            }

            User user = userOpt.get();
            HomeTask task = taskOpt.get();

            // 2. Seguridad: ¿El usuario pertenece a la casa de la tarea?
            if (user.getHomeId() == null || !user.getHomeId().equals(task.getHomeId())) {
                return Result.failure(HomeErrors.UNAUTHORIZED_ACCESS);
            }

            // 3. Actualizar estado
            task.setStatus(command.status());

            // 4. Gestionar fecha de completado
            if (command.status() == TaskStatus.done) { // Ajusta a tu Enum (ej. TaskStatus.done si está en minúscula)
                task.setCompletedAt(LocalDateTime.now());
            } else {
                task.setCompletedAt(null);
            }

            homeTaskRepository.save(task);

            return Result.success(null);

        } catch (IllegalArgumentException e) {
            System.out.println("Error al actualizar la tarea, estado no válido");
            return Result.failure(HomeErrors.TASK_UPDATE_FAILED);
        } catch (Exception e) {
            System.out.println("Error al actualizar estado de la tarea: " + e.getMessage());
            return Result.failure(HomeErrors.TASK_UPDATE_FAILED);
        }
    }
}
