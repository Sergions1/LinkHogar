package com.linkhogar.application.event.create;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventErrors;
import com.linkhogar.domain.event.HomeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateEventCommandHandler {

    private final HomeEventRepository homeEventRepository;
    private final SimpMessagingTemplate messagingTemplate;
    // private final BrevoEmailService emailService; <-- todo Inyecta aquí tu servicio de Brevo

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

        // 4. DISPARADORES INMEDIATOS (Notificaciones)

        // A) Aviso en tiempo real a la app para recargar el calendario
        messagingTemplate.convertAndSend("/topic/home." + command.homeId() + ".events", "RELOAD");

        // B) todo Enviar correo de "Nuevo evento creado en tu casa"
        // emailService.sendNewEventEmail(command.homeId(), newEvent);

        return Result.success(newEvent.getId());
    }
}