package com.linkhogar.domain.house;

import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "house_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "house_id", nullable = false)
    private UUID houseId;

    @Column(name = "user_id", nullable = false)
    private UUID userId; // El usuario que hace la denuncia

    @Column(name = "user_name")
    private String userName;

    @Column(nullable = false)
    private String reason; // Ej: "Fraude", "Contenido Inapropiado", "Ya no está disponible"

    @Column(length = 500)
    private String description; // Detalles extra que escriba el usuario

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private ReportStatus status;
}