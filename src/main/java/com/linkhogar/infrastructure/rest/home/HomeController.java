package com.linkhogar.infrastructure.rest.home;

import com.linkhogar.application.home.addMember.AddMemberRequest;
import com.linkhogar.application.home.addMember.AddMemberToHomeCommand;
import com.linkhogar.application.home.addMember.AddMemberToHomeCommandHandler;
import com.linkhogar.application.home.removeMember.RemoveMemberFromHomeCommand;
import com.linkhogar.application.home.removeMember.RemoveMemberFromHomeCommandHandler;
import com.linkhogar.domain.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/homes")
@RequiredArgsConstructor
@Tag(name = "Home", description = "Gestión de hogares y convivencia")
public class HomeController {

    private final AddMemberToHomeCommandHandler addMemberToHomeCommandHandler;
    private final RemoveMemberFromHomeCommandHandler removeMemberFromHomeCommandHandler;

    @PostMapping("/{homeId}/members")
    @Operation(summary = "Añadir un usuario al hogar mediante su correo electrónico")
    public ResponseEntity<?> addMember(
            @PathVariable UUID homeId,
            @RequestBody AddMemberRequest request,
            Authentication authentication) {

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UUID requesterId = UUID.fromString(authentication.getName());

        AddMemberToHomeCommand command = new AddMemberToHomeCommand(homeId, request.email(), requesterId);
        Result<Void> result = addMemberToHomeCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }

    @DeleteMapping("/{homeId}/members/{memberId}")
    @Operation(summary = "Eliminar a un usuario del hogar")
    public ResponseEntity<?> removeMember(
            @PathVariable UUID homeId,
            @PathVariable UUID memberId,
            Authentication authentication) {

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UUID requesterId = UUID.fromString(authentication.getName());

        RemoveMemberFromHomeCommand command = new RemoveMemberFromHomeCommand(homeId, memberId, requesterId);
        Result<Void> result = removeMemberFromHomeCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result.getError());
        }
    }
}