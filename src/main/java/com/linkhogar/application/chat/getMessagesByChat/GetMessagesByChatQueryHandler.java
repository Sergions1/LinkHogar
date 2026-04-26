package com.linkhogar.application.chat.getMessagesByChat;

import com.linkhogar.domain.chat.ChatParticipantRepository;
import com.linkhogar.domain.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMessagesByChatQueryHandler {
    private final MessageRepository messageRepository;
    private final ChatParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public List<MessageResponse> handle(GetMessagesByChatQuery query) {

        boolean isParticipant = participantRepository.findByChatId(query.chatId()).stream()
                .anyMatch(p -> p.getUserId().equals(query.userId()));

        if (!isParticipant) {
            throw new RuntimeException("No tienes acceso a este chat");
        }

        Pageable pageable = PageRequest.of(query.page(), query.size());

        List<MessageResponse> messages = messageRepository.findByChatId(query.chatId(), pageable)
                .stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        query.chatId(),
                        msg.getSenderId(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }
}
