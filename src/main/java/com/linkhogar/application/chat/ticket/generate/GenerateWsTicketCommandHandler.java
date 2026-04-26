package com.linkhogar.application.chat.ticket.generate;

import com.linkhogar.domain.chat.ticket.WsTicket;
import com.linkhogar.domain.chat.ticket.WsTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateWsTicketCommandHandler {

    private final WsTicketRepository ticketRepository;

    public String handle(GenerateWsTicketCommand command) {
        String ticketId = UUID.randomUUID().toString();
        // El ticket vive 15 segundos
        WsTicket ticket = new WsTicket(ticketId, command.userId(), System.currentTimeMillis() + 15000);

        ticketRepository.save(ticket);
        return ticketId;
    }
}