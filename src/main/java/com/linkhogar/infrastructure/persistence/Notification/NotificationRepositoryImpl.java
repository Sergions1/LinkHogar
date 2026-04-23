package com.linkhogar.infrastructure.persistence.Notification;

import com.linkhogar.domain.common.Notification;
import com.linkhogar.domain.common.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaRepository;

    @Override
    public void save(Notification notification) {
        jpaRepository.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(UUID userId) {
        return jpaRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    public Page<Notification> getAllByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Optional<Notification> getById(UUID id) {
        return jpaRepository.findById(id);
    }
}
