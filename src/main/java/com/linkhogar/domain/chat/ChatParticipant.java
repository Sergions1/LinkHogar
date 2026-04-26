package com.linkhogar.domain.chat;

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
 @Table(name = "chat_participant")
public class ChatParticipant {
     @Id
    private UUID id;
    private UUID chatId;
    private UUID userId;

    private LocalDateTime joinedAt;

    private LocalDateTime lastReadAt; //Indicará en que momento leyó por ultima vez, lo que permitirá la gestion de mensajes no leidos
}