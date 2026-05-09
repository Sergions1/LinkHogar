package com.linkhogar.application.event.create;

import com.linkhogar.application.homeTask.getHomeMembers.GetHomeMembersQuery;
import com.linkhogar.application.homeTask.getHomeMembers.GetHomeMembersQueryHandler;
import com.linkhogar.application.homeTask.getHomeMembers.HomeMemberResponse;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommand;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommandHandler;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventErrors;
import com.linkhogar.domain.event.HomeEventRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateEventCommandHandler {

    private final HomeEventRepository homeEventRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CreateNotificationCommandHandler notificationHandler;
    private final MailService mailService;
    private final GetHomeMembersQueryHandler getHomeMembersQueryHandler;

    @Transactional
    public Result<UUID> handle(CreateEventCommand command) {

        // 1. Validaciones básicas
        if (command.title() == null || command.title().trim().isEmpty()) {
            return Result.failure(HomeEventErrors.TTITLE_REQUIRED);
        }
        if (command.startDate() == null) {
            return Result.failure(HomeEventErrors.INVALID_DATES);
        }

        // 2. Construir la entidad
        HomeEvent newEvent = new HomeEvent();
        newEvent.setHomeId(command.homeId());
        newEvent.setCreatorId(command.creatorId());
        newEvent.setCreatorName(command.creatorName());
        newEvent.setTitle(command.title());
        newEvent.setDescription(command.description());
        newEvent.setStartDate(command.startDate());
        newEvent.setEndDate(command.endDate());
        newEvent.setAllDay(command.allDay());
        newEvent.setReminderMinutesBefore(command.reminderMinutesBefore());
        newEvent.setReminderSent(false); // Por defecto no se ha enviado
        newEvent.setCreatedAt(LocalDateTime.now());

        // 3. Guardar en Base de Datos
        homeEventRepository.save(newEvent);

        messagingTemplate.convertAndSend("/topic/home." + command.homeId() + ".events", "REFRESH");

        Result<List<HomeMemberResponse>> result = getHomeMembersQueryHandler.handle(new GetHomeMembersQuery(command.homeId(), command.creatorId()));

        List<HomeMemberResponse> homeMembers = result.getValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = command.startDate().format(formatter);

        for (HomeMemberResponse member : homeMembers) {
            // No notificamos al que ha creado el evento
            if (member.id().equals(command.creatorId())) {
                continue;
            }

            String title = "Nuevo evento en el calendario";
            String message = command.creatorName() + " ha programado el evento: '" + command.title() + "' para el " + formattedDate + ".";

            // B) Notificación In-App (Campanita)
            notificationHandler.handle(new CreateNotificationCommand(
                    member.id(),
                    title,
                    message
            ));

            // C) Notificación en Tiempo Real (Campanita WebSocket)
            Map<String, String> wsNotification = new HashMap<>();
            wsNotification.put("title", title);
            wsNotification.put("message", message);
            wsNotification.put("type", "EVENT");
            messagingTemplate.convertAndSend("/topic/user." + member.id(), wsNotification);

            // D) Correo Electrónico
            if (member.email() != null) {
                mailService.sendNewEventEmail(
                        member.email(),
                        command.creatorName(),
                        command.title(),
                        formattedDate
                );
            }
        }

        return Result.success(newEvent.getId());
    }
}