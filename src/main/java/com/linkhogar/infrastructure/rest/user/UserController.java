package com.linkhogar.infrastructure.rest.user;

import com.linkhogar.application.user.changePassword.ChangePasswordCommand;
import com.linkhogar.application.user.changePassword.ChangePasswordCommandHandler;
import com.linkhogar.application.user.changePassword.ChangePasswordRequest;
import com.linkhogar.application.user.create.CreateUserCommand;
import com.linkhogar.application.user.create.CreateUserCommandHandler;
import com.linkhogar.application.user.delete.DeleteUserCommand;
import com.linkhogar.application.user.delete.DeleteUserCommandHandler;
import com.linkhogar.application.user.getAll.GetAllQuery;
import com.linkhogar.application.user.getAll.GetAllQueryHandler;
import com.linkhogar.application.user.getById.GetUserByIdQueryHandler;
import com.linkhogar.application.user.getById.GetUserByIdQuery;
import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQuery;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQueryHandler;
import com.linkhogar.application.user.toggleUserEnabled.ToggleUserEnabledCommand;
import com.linkhogar.application.user.toggleUserEnabled.ToggleUserEnabledHandler;
import com.linkhogar.application.user.update.UpdateUserCommand;
import com.linkhogar.application.user.update.UpdateUserCommandHandler;
import com.linkhogar.application.user.update.UserUpdateDTO;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.common.result.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {
    private final CreateUserCommandHandler createUserCommandHandler;
    private final GetUserByIdQueryHandler getUserByIdQueryHandler;
    private final DeleteUserCommandHandler deleteUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final GetCurrentUserQueryHandler getCurrentUserQueryHandler;
    private final GetAllQueryHandler getAllQueryHandler;
    private final ToggleUserEnabledHandler toggleUserEnabledHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;

    @Operation(
            summary = "Obtener usuario por Id",
            description = "Recupera la información pública de un usuario dado su UUID. No devuelve la contraseña."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        GetUserByIdQuery query = new GetUserByIdQuery(id);

        var result = getUserByIdQueryHandler.handle(query);

        if(result.isSuccess()){
            return ResponseEntity.ok(result.getValue());
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Eliminar usuario por Id",
            description = "Elimina un usuario por su Id, si este existe previamente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id){
        DeleteUserCommand command = new DeleteUserCommand(id);
        Result<Void> result = deleteUserCommandHandler.handle(command);

        if (result.isSuccess()){
            return ResponseEntity.noContent().build(); //Estandar para los delete exitosos
        }

        return mapErrorToResponse(result.getError());
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualizado de usuario pasandole el id de Usuario y los datos a traves de JSON"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateDTO updateDTO,
            Authentication authentication) {

        // Obtenemos el rol del usuario que hace la petición
        String requestingUserRole = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        // Solo LinkHogar puede cambiar el rol
        String roleToSet = requestingUserRole.equals("LinkHogar")
                ? updateDTO.role()
                : null;

        UpdateUserCommand command = new UpdateUserCommand(
                id,
                updateDTO.firstName(),
                updateDTO.lastName(),
                updateDTO.fecha_Nac(),
                roleToSet,
                updateDTO.phone()
        );

        Result<Void> result = updateUserCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }

        return mapErrorToResponse(result.getError());
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        GetCurrentUserQuery query = new GetCurrentUserQuery(authentication.getName());
        return ResponseEntity.ok(getCurrentUserQueryHandler.handle(query));
    }

    @GetMapping
    @Operation(summary = "Obtiene lista paginada de usuarios con filtros opcionales")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled,
            Authentication authentication) {

        GetAllQuery query = new GetAllQuery(page, size, search, role, enabled);
        return ResponseEntity.ok(getAllQueryHandler.handle(query));
    }

    @PatchMapping("/{id}/toggle-enabled")
    @Operation(summary = "Activa o desactiva un usuario")
    public ResponseEntity<Void> toggleEnabled(
            @PathVariable UUID id,
            Authentication authentication) {

        ToggleUserEnabledCommand command = new ToggleUserEnabledCommand(id);
        toggleUserEnabledHandler.handle(command);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        // Sacamos el email directamente del token por seguridad
        ChangePasswordCommand command = new ChangePasswordCommand(
                request.mail(),
                request.code(),
                request.newPassword()
        );

        changePasswordCommandHandler.handle(command);

        return ResponseEntity.ok().build();
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
