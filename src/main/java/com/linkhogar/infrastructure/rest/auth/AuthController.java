package com.linkhogar.infrastructure.rest.auth;

import com.linkhogar.application.user.create.CreateUserCommand;
import com.linkhogar.application.user.create.CreateUserCommandHandler;
import com.linkhogar.application.user.getPasswordCode.GetPasswordCodeQuery;
import com.linkhogar.application.user.getPasswordCode.GetPasswordCodeQueryHandler;
import com.linkhogar.application.user.login.UserLoginCommand;
import com.linkhogar.application.user.login.UserLoginCommandHandler;
import com.linkhogar.application.user.resetPassword.ResetPasswordCommand;
import com.linkhogar.application.user.resetPassword.ResetPasswordCommandHandler;
import com.linkhogar.application.user.verify.VerifyUserCommand;
import com.linkhogar.application.user.verify.VerifyUserCommandHandler;
import com.linkhogar.application.user.verifyPasswordCode.VerifyPasswordCodeQuery;
import com.linkhogar.application.user.verifyPasswordCode.VerifyPasswordCodeQueryHandler;
import com.linkhogar.domain.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authorize", description = "Operaciones de autorización")
public class AuthController {
    private final UserLoginCommandHandler userLoginCommandHandler;
    private final CreateUserCommandHandler createUserCommandHandler;
    private final VerifyUserCommandHandler verifyUserCommandHandler;
    private final GetPasswordCodeQueryHandler getPasswordCodeQueryHandler;
    private final VerifyPasswordCodeQueryHandler verifyPasswordCodeQueryHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;

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
        try {
            createUserCommandHandler.handle(command);
            return ResponseEntity.ok("Registro exitoso. Por favor, revisa tu correo para verificar la cuenta.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        try {
            verifyUserCommandHandler.handle(new VerifyUserCommand(token));
            return ResponseEntity.ok("Cuenta verificada con éxito. Ya puedes iniciar sesión.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/request-password-code")
    public ResponseEntity<String> requestPasswordCode(@RequestBody GetPasswordCodeQuery query){
        try{
            getPasswordCodeQueryHandler.handle(query);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-password-code")
    public ResponseEntity<String> verifyPasswordCode(@RequestBody VerifyPasswordCodeQuery query) {

        try{
            verifyPasswordCodeQueryHandler.handle(query);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPasswordOutside(@RequestBody ResetPasswordCommand command) {
        try {
            resetPasswordCommandHandler.handle(command);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // Si el código falla o expira
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
