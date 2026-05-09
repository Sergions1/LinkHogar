package com.linkhogar.application.homeTask.create;

import com.linkhogar.application.notifications.createNotification.CreateNotificationCommand;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommandHandler;
import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeTask;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import com.linkhogar.domain.homeTasks.enums.TaskStatus;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateHomeTaskCommandHandler {
    private final HomeTaskRepository homeTaskRepository;
    private final UserRepository userRepository;
    private final CreateNotificationCommandHandler notificationHandler;
    private final SimpMessagingTemplate messagingTemplate;
    private final MailService mailService;

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

            messagingTemplate.convertAndSend("/topic/home." + command.homeId() + ".tasks", "REFRESH");

            // 2. Si la tarea se ha asignado a alguien y no es a uno mismo
            if (command.assignedUserId() != null && !command.assignedUserId().equals(command.createdBy())) {

                String title = "Nueva tarea asignada";
                String message = command.createdByName() + " te ha asignado la tarea: '" + command.title() + "'.";

                // A) Notificación In-App (Campanita)
                notificationHandler.handle(new CreateNotificationCommand(
                        command.assignedUserId(),
                        title,
                        message
                ));

                // B) Notificación Tiempo Real (Campanita WebSocket)
                Map<String, String> wsNotification = new HashMap<>();
                wsNotification.put("title", title);
                wsNotification.put("message", message);
                wsNotification.put("type", "TASK");
                messagingTemplate.convertAndSend("/topic/user." + command.assignedUserId(), wsNotification);

                // C) Correo Electrónico
                userRepository.userById(command.assignedUserId()).ifPresent(assignee -> {
                    if (assignee.getMail() != null) {
                        mailService.sendNewTaskEmail(
                                assignee.getMail(),
                                command.createdByName(),
                                command.title()
                        );
                    }
                });
            }

            HomeTask savedTask = homeTaskRepository.save(task);

            return Result.success(savedTask.getId());
        } catch (Exception e) {
            System.out.println("Error al crear la tarea del hogar: " + e.getMessage());
            return Result.failure(Error.failure("500", "No se pudo crear la tarea"));
        }
    }
}
