package com.linkhogar.domain.event;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "home_event")
@Data
public class HomeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "home_id", nullable = false)
    private UUID homeId;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_all_day", nullable = false)
    private boolean allDay;

    @Column(name = "reminder_minutes_before", nullable = false)
    private int reminderMinutesBefore; // 0 = sin aviso, 60 = 1h antes, 1440 = 1 día antes

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}