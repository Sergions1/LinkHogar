package com.linkhogar.infrastructure.persistence.chat.ticket;

import com.linkhogar.domain.chat.ticket.WsTicket;
import com.linkhogar.domain.chat.ticket.WsTicketRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryWsTicketRepository implements WsTicketRepository {

    private final Map<String, WsTicket> store = new ConcurrentHashMap<>();

    @Override
    public void save(WsTicket ticket) {
        store.put(ticket.id(), ticket);
    }

    @Override
    public Optional<WsTicket> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}