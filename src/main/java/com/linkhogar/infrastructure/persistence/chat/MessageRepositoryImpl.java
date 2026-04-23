package com.linkhogar.infrastructure.persistence.chat;

import com.linkhogar.domain.chat.Message;
import com.linkhogar.domain.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final JpaMessageRepository jpaMessageRepository;

    @Override
    public Message save(Message message) {
        return jpaMessageRepository.save(message);
    }
}