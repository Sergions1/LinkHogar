package com.linkhogar.domain.chat;

import com.linkhogar.domain.chat.enums.ChatType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private ChatType type;

    // El ID de la casa, del hogar o del ticket, según el tipo
    private UUID referenceId;

    private LocalDateTime createdAt;
}