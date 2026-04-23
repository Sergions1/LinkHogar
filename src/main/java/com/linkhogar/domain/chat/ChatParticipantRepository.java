package com.linkhogar.domain.chat;

import java.util.List;

public interface ChatParticipantRepository {
    List<ChatParticipant> saveAll(List<ChatParticipant> participants);
}