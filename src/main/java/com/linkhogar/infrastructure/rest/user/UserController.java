package com.linkhogar.infrastructure.rest.user;

import com.linkhogar.application.user.create.CreateUserCommand;
import com.linkhogar.application.user.create.CreateUserCommandHandler;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.common.result.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final CreateUserCommandHandler createUserCommandHandler;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody CreateUserCommand request){
        Result<UUID> result = createUserCommandHandler.handle(request);

        if(result.isSuccess()){
            return ResponseEntity.created(URI.create("/users/"+result.getValue())).body(result.getValue());
        }

        return mapErrorToResponse(result.getError());
    }

    private ResponseEntity<?> mapErrorToResponse(Error error) {
        HttpStatus status = switch (error.type()) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;       // 400
            case NOT_FOUND -> HttpStatus.NOT_FOUND;          // 404
            case CONFLICT -> HttpStatus.CONFLICT;            // 409
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;    // 401
            case FAILURE -> HttpStatus.INTERNAL_SERVER_ERROR;// 500
        };

        // Devolvemos un objeto JSON con el detalle del error
        return ResponseEntity
                .status(status)
                .body(error); // Jackson convertirá el record Error a JSON automáticamente
    }
}
