package com.linkhogar.domain.homeTasks;

import com.linkhogar.domain.homeTasks.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "home_task")
@Data
public class HomeTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "home_id", nullable = false)
    private UUID homeId;

    @Column(name = "assigned_user_id")
    private UUID assignedUserId;

    @Column(name = "assigned_user_name")
    private String assignedUserName;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Se rellena solo al crear

    @Column(name = "start_date")
    private LocalDateTime startDate; // Puede ser nulo para tareas puntuales

    @Column(name = "due_date")
    private LocalDateTime dueDate; // Fecha límite / Fecha del evento puntual

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
