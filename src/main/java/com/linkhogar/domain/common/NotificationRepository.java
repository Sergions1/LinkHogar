package com.linkhogar.domain.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    void save(Notification notification);
    List<Notification> getUnreadNotifications(UUID userId);
    Page<Notification> getAllByUserId(UUID userId, Pageable pageable);
    Optional<Notification> getById(UUID id);
}
