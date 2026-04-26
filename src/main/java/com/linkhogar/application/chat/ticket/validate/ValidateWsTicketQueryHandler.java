package com.linkhogar.application.chat.ticket.validate;

import com.linkhogar.domain.chat.ticket.WsTicket;
import com.linkhogar.domain.chat.ticket.WsTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidateWsTicketQueryHandler {

    private final WsTicketRepository ticketRepository;

    public UUID handle(ValidateWsTicketQuery query) {
        Optional<WsTicket> ticketOpt = ticketRepository.findById(query.ticketId());

        if (ticketOpt.isEmpty()) {
            return null;
        }

        WsTicket ticket = ticketOpt.get();


        if (ticket.isExpired()) {
            ticketRepository.delete(ticket.id());
            return null;
        }

        return ticket.userId();
    }
}