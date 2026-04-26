package com.linkhogar.infrastructure.rest.chat;

import com.linkhogar.application.chat.ticket.generate.GenerateWsTicketCommand;
import com.linkhogar.application.chat.ticket.generate.GenerateWsTicketCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat/ticket")
@RequiredArgsConstructor
public class WsTicketController {

    private final GenerateWsTicketCommandHandler generateHandler;

    @PostMapping
    public ResponseEntity<?> generateTicket(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        String ticket = generateHandler.handle(new GenerateWsTicketCommand(userId));
        return ResponseEntity.ok(Map.of("ticket", ticket));
    }
}