package com.linkhogar.domain.chat;

import com.linkhogar.domain.chat.enums.ChatStatus;
import com.linkhogar.domain.chat.enums.ChatType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat")
public class Chat {
    @Id
    private UUID id;

    // Esto nos permite separar la lógica visual después
    @Enumerated(EnumType.STRING)
    private ChatType type;

    // El ID de la casa, del hogar o del ticket, según el tipo
    private UUID referenceId;

    private LocalDateTime createdAt;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatStatus status = ChatStatus.Activo;
}