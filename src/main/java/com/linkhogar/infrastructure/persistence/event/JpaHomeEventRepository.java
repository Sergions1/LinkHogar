package com.linkhogar.infrastructure.persistence.event;

import com.linkhogar.domain.event.HomeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaHomeEventRepository extends JpaRepository<HomeEvent, UUID> {
    List<HomeEvent> findByHomeIdOrderByStartDateAsc(UUID homeId);

    // Busca eventos futuros que tengan recordatorio configurado y aún no se haya enviado
    @Query("SELECT e FROM HomeEvent e WHERE e.reminderSent = false AND e.reminderMinutesBefore > 0")
    List<HomeEvent> findPendingReminders();
}
