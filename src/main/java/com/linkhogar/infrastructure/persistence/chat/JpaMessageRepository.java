package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaMessageRepository extends JpaRepository<Message, UUID> {
    // Aquí en el futuro añadiremos el méodo para cargar mensajes por chatId ordenados por fecha
}