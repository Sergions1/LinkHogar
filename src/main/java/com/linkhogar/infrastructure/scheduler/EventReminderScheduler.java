package com.linkhogar.infrastructure.scheduler;

import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final HomeEventRepository homeEventRepository;
    private final MailService mailService;
    //private final WebS webSocketService; (Tu servicio para notificar a la app)

    // Se ejecuta cada minuto (segundo 0)
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processEventReminders() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<HomeEvent> pendingEvents = homeEventRepository.findPendingReminders(now);

        for (HomeEvent event : pendingEvents) {
            // Calculamos en qué momento exacto debería saltar la alarma
            LocalDateTime triggerTime = event.getStartDate().minusMinutes(event.getReminderMinutesBefore());

            if (!now.isBefore(triggerTime)) {

                //Enviar notificación WebSocket
                // webSocketService.sendNotification(event.getHomeId(), "Recordatorio: " + event.getTitle());

                //Enviar correo vía Brevo
                //mailService.sendEventReminder(event.getHomeId(), event);

                //Marcar como enviado para no repetir
                event.setReminderSent(true);
                homeEventRepository.save(event);

                log.info("Recordatorio enviado para el evento: {}", event.getTitle());
            }
        }
    }
}