package com.linkhogar.infrastructure.scheduler;

import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final HomeEventRepository homeEventRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processEventReminders() {
        List<HomeEvent> pendingEvents = homeEventRepository.findPendingReminders();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HomeEvent event : pendingEvents) {


            List<User> members = userRepository.findByHome(event.getHomeId());

            if (members != null &&  !members.isEmpty()) {
                String formattedDate = event.getStartDate().format(formatter);

                for (User member : members) {
                    mailService.sendEventReminderEmail(
                            member.getMail(),
                            event.getTitle(),
                            formattedDate
                    );
                }
            }

            event.setReminderSent(true);
            homeEventRepository.save(event);

            log.info("Recordatorio enviado para el evento: {} a todos los miembros", event.getTitle());

        }
    }
}