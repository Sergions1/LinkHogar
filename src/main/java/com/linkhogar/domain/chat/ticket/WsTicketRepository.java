package com.linkhogar.domain.chat.ticket;

import java.util.Optional;

public interface WsTicketRepository {
    void save(WsTicket ticket);
    Optional<WsTicket> findById(String id);
    void delete(String id);
}