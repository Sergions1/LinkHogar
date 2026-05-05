package com.linkhogar.infrastructure.rest.event;

import com.linkhogar.application.event.create.CreateEventCommand;
import com.linkhogar.application.event.create.CreateEventCommandHandler;
import com.linkhogar.application.event.getByHome.GetHomeEventsQuery;
import com.linkhogar.application.event.getByHome.GetHomeEventsQueryHandler;
import com.linkhogar.application.event.getByHome.HomeEventResponse;
import com.linkhogar.application.event.update.UpdateEventCommand;
import com.linkhogar.application.event.update.UpdateEventCommandHandler;
import com.linkhogar.application.event.update.UpdateEventRequest;
import com.linkhogar.domain.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Gestor del calendario y eventos del hogar")
public class HomeEventController {

    private final CreateEventCommandHandler createEventCommandHandler;
    private final GetHomeEventsQueryHandler getHomeEventsQueryHandler;
    private final UpdateEventCommandHandler updateEventCommandHandler;

    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo evento en el calendario")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventCommand command, Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Result<UUID> result = createEventCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of("id", result.getValue()));
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }

    }

    @GetMapping("/home/{homeId}")
    @Operation(summary = "Obtener todos los eventos de una casa")
    public ResponseEntity<?> getHomeEvents(@PathVariable UUID homeId, Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        GetHomeEventsQuery query = new GetHomeEventsQuery(homeId);
        Result<List<HomeEventResponse>> result = getHomeEventsQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.badRequest().body(result.getError());
        }

    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Editar un evento existente")
    public ResponseEntity<?> updateEvent(
            @PathVariable UUID eventId,
            @RequestBody UpdateEventRequest request,
            Authentication authentication) {

        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Mapeamos el DTO de la red al Comando de negocio
        UpdateEventCommand command = new UpdateEventCommand(
                eventId,
                request.homeId(),
                request.title(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.allDay(),
                request.reminderMinutesBefore()
        );

        Result<Void> result = updateEventCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }
}