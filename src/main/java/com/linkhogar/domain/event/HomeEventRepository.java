package com.linkhogar.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeEventRepository {
    HomeEvent save(HomeEvent event);
    List<HomeEvent> findByHomeIdOrderByStartDateAsc(UUID homeId);
    List<HomeEvent> findPendingReminders();
    void deleteEvent(UUID eventId);
    Optional<HomeEvent> findById(UUID eventId);
}
