package com.linkhogar.infrastructure.rest.auth;

import com.linkhogar.application.user.create.CreateUserCommand;
import com.linkhogar.application.user.create.CreateUserCommandHandler;
import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQuery;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQueryHandler;
import com.linkhogar.application.user.login.UserLoginCommand;
import com.linkhogar.application.user.login.UserLoginCommandHandler;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authorize", description = "Operaciones de autorización")
public class AuthController {
    private final UserLoginCommandHandler userLoginCommandHandler;
    private final CreateUserCommandHandler createUserCommandHandler;


    @Operation(
            summary = "Inicio de sesion de un usuario"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginCommand command) {
        Result<String> result = userLoginCommandHandler.handle(command);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(result.getError());
        }

        return ResponseEntity.ok(result.getValue());
    }

    @Operation(
            summary = "Registro de un usuario",
            description = "Necesarios nombre, apellidos, email, contraseña y fecha de nacimiento"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateUserCommand command) {
        Result<UUID> result = createUserCommandHandler.handle(command);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getError());
        }

        return ResponseEntity.ok(result.getValue());
    }

}
