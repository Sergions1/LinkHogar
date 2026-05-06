package com.linkhogar.infrastructure.persistence.event;

import com.linkhogar.domain.event.HomeEvent;
import com.linkhogar.domain.event.HomeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HomeEventoRepositoryImpl implements HomeEventRepository {

    private final JpaHomeEventRepository jpaHomeEventRepository;

    @Override
    public HomeEvent save(HomeEvent event) {
        return jpaHomeEventRepository.save(event);
    }

    @Override
    public List<HomeEvent> findByHomeIdOrderByStartDateAsc(UUID homeId) {
        return jpaHomeEventRepository.findByHomeIdOrderByStartDateAsc(homeId);
    }

    @Override
    public List<HomeEvent> findPendingReminders(LocalDateTime now) {
        return jpaHomeEventRepository.findPendingReminders(now);
    }

    @Override
    public void deleteEvent(UUID eventId) {
        jpaHomeEventRepository.deleteById(eventId);
    }

    @Override
    public Optional<HomeEvent> findById(UUID eventId) {
        return jpaHomeEventRepository.findById(eventId);
    }
}
