package com.linkhogar.infrastructure.rest.user;

import com.linkhogar.application.house.getByCity.HouseCardResponse;
import com.linkhogar.application.user.addHouseFavourite.AddHouseFavouriteCommand;
import com.linkhogar.application.user.addHouseFavourite.AddHouseFavouriteCommandHandler;
import com.linkhogar.application.user.changePassword.ChangePasswordCommand;
import com.linkhogar.application.user.changePassword.ChangePasswordCommandHandler;
import com.linkhogar.application.user.changePassword.ChangePasswordRequest;
import com.linkhogar.application.user.create.CreateUserCommandHandler;
import com.linkhogar.application.user.delete.DeleteUserCommand;
import com.linkhogar.application.user.delete.DeleteUserCommandHandler;
import com.linkhogar.application.user.deleteHouseFavourite.DeleteHouseFAvouriteCommandHandler;
import com.linkhogar.application.user.deleteHouseFavourite.DeleteHouseFavouriteCommand;
import com.linkhogar.application.user.getAll.GetAllQuery;
import com.linkhogar.application.user.getAll.GetAllQueryHandler;
import com.linkhogar.application.user.getById.GetUserByIdQueryHandler;
import com.linkhogar.application.user.getById.GetUserByIdQuery;
import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQuery;
import com.linkhogar.application.user.getCurrentUser.GetCurrentUserQueryHandler;
import com.linkhogar.application.user.getFavouriteHouses.GetFavouriteHousesQuery;
import com.linkhogar.application.user.getFavouriteHouses.GetFavouriteHousesQueryHandler;
import com.linkhogar.application.user.getUserFavourites.GetUserFavouriteQuery;
import com.linkhogar.application.user.getUserFavourites.GetUserFavouritesQueryHandler;
import com.linkhogar.application.user.toggleUserEnabled.ToggleUserEnabledCommand;
import com.linkhogar.application.user.toggleUserEnabled.ToggleUserEnabledHandler;
import com.linkhogar.application.user.update.UpdateUserCommand;
import com.linkhogar.application.user.update.UpdateUserCommandHandler;
import com.linkhogar.application.user.update.UserUpdateDTO;
import com.linkhogar.application.user.updateAvatar.UpdateAvatarCommand;
import com.linkhogar.application.user.updateAvatar.UpdateAvatarCommandHandler;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.common.result.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
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
    private final UpdateAvatarCommandHandler updateAvatarCommandHandler;
    private final AddHouseFavouriteCommandHandler addHouseFavouriteCommandHandler;
    private final GetUserFavouritesQueryHandler getUserFavouriteQueryHandler;
    private final DeleteHouseFAvouriteCommandHandler deleteHouseFavouriteCommandHandler;
    private final GetFavouriteHousesQueryHandler getFavouriteHousesQueryHandlerHandler;

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
    @PutMapping("update/{id}")
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

        UpdateUserCommand command = new UpdateUserCommand(
                id,
                updateDTO.firstName(),
                updateDTO.lastName(),
                updateDTO.fecha_Nac(),
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

    @PostMapping("/uploadAvatar/{id}")
    public ResponseEntity<Void> uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file, Authentication authentication) {
       if(authentication == null)
       {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }

       Result<Void> result = updateAvatarCommandHandler.handle(new UpdateAvatarCommand(id, file));

       if (result.isSuccess()) {
           return ResponseEntity.ok().build();
       }else{
           return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
       }
    }

    @PostMapping("/addFavourite/{userId}/{houseId}")
    public ResponseEntity<Void> addFavourite(@PathVariable UUID userId,@PathVariable UUID houseId ,Authentication authentication) {
        if(authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AddHouseFavouriteCommand command = new AddHouseFavouriteCommand(
                userId,
                houseId
        );

        Result<Void> result = addHouseFavouriteCommandHandler.handle(command);
        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }else{
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/favourites/ids/{userId}")
    public ResponseEntity<List<UUID>> getFavouriteIds(@PathVariable UUID userId, Authentication authentication) {
        if(authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GetUserFavouriteQuery query = new GetUserFavouriteQuery(userId);

        List<UUID> ids = getUserFavouriteQueryHandler.handle(query);

        return ResponseEntity.ok(ids);
    }

    @DeleteMapping("/deleteFavourite/{userId}/{houseId}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable UUID userId,@PathVariable UUID houseId ,Authentication authentication) {
        if(authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DeleteHouseFavouriteCommand command = new DeleteHouseFavouriteCommand(
                userId,
                houseId
        );

        Result<Void> result = deleteHouseFavouriteCommandHandler.handler(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }else {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/getPaginatedFavourites/{userId}")
    public ResponseEntity<Page<HouseCardResponse>> getPaginatedHouseCards(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if(authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GetFavouriteHousesQuery query = new GetFavouriteHousesQuery(userId, page, size);

        Result<Page<HouseCardResponse>> result = getFavouriteHousesQueryHandlerHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }else  {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
