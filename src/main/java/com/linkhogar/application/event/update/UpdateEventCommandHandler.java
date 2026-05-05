package com.linkhogar.application.event.update;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventErrors;
import com.linkhogar.domain.event.HomeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateEventCommandHandler {

    private final HomeEventRepository homeEventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Result<Void> handle(UpdateEventCommand command) {

        // 1. Validaciones básicas
        if (command.title() == null || command.title().trim().isEmpty()) {
            return Result.failure(HomeEventErrors.TTITLE_REQUIRED);
        }
        if (command.startDate() == null) {
            return Result.failure(HomeEventErrors.INVALID_DATES);
        }

        // 2. Buscar el evento
        Optional<HomeEvent> eventOpt = homeEventRepository.findById(command.eventId());
        if (eventOpt.isEmpty()) {
            return Result.failure(HomeEventErrors.NotFound(command.eventId()));
        }

        HomeEvent event = eventOpt.get();

        // 3. Seguridad: Verificar que el evento pertenece a la casa del usuario que hace la petición
        if (!event.getHomeId().equals(command.homeId())) {
            return Result.failure(HomeEventErrors.UNAUTHORIZED_ACCESS);
        }

        // 4. Lógica de negocio para los Recordatorios
        // Si han cambiado la fecha de inicio o los minutos del aviso, reactivamos la alarma
        boolean resetReminder = !event.getStartDate().equals(command.startDate())
                || event.getReminderMinutesBefore() != command.reminderMinutesBefore();

        // 5. Actualizar los datos
        event.setTitle(command.title());
        event.setDescription(command.description());
        event.setStartDate(command.startDate());
        event.setEndDate(command.endDate());
        event.setAllDay(command.allDay());
        event.setReminderMinutesBefore(command.reminderMinutesBefore());

        if (resetReminder) {
            event.setReminderSent(false); // Reactiva el despertador
        }

        homeEventRepository.save(event);

        // 6. Notificar por WebSocket para que los demás lo vean actualizarse mágicamente
        messagingTemplate.convertAndSend("/topic/home." + command.homeId() + ".events", "RELOAD");

        return Result.success(null);
    }
}