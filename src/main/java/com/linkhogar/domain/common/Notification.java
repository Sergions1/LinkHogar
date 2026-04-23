package com.linkhogar.domain.common;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId; // A quién va dirigida la notificación

    @Column(nullable = false)
    private String title; // Ej: "Anuncio Retirado"

    @Column(nullable = false, length = 500)
    private String message; // Ej: "Tu anuncio 'Piso en Madrid' ha sido ocultado por un administrador."

    @Column(name = "is_read", nullable = false)
    private boolean isRead; // Para saber si mostramos el puntito rojo en la campana

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}