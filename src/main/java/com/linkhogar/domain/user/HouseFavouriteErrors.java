package com.linkhogar.domain.user;

import com.linkhogar.domain.common.result.Error;
import java.util.UUID;

public class HouseFavouriteErrors {

    private HouseFavouriteErrors() {}

    public static Error notFound(UUID favouriteId) {
        return Error.notFound(
                "HouseFavourites.NotFound",
                "El favorito con el Id = '" + favouriteId + "' no fue encontrado"
        );
    }

    public static Error notFound(UUID userId, UUID houseId) {
        return Error.notFound(
                "HouseFavourites.NotFoundByUserAndHouse",
                "El favorito para el usuario '" + userId + "' y la vivienda '" + houseId + "' no fue encontrado"
        );
    }

    public static Error notExist() {
        return Error.notFound(
                "HouseFavourites.NotExist",
                "El ususario no tienen ningun favorito"
        );
    }

    public static Error alreadyExists(UUID userId, UUID houseId) {
        return Error.conflict(
                "HouseFavourites.AlreadyExists",
                "El usuario '" + userId + "' ya tiene la vivienda '" + houseId + "' en sus favoritos"
        );
    }

    public static final Error UNAUTHORIZED = Error.unauthorized(
            "HouseFavourites.Unauthorized",
            "No tienes permisos para modificar o eliminar este favorito"
    );

    public static final Error INVALID_HOUSE = Error.conflict(
            "HouseFavourites.InvalidHouse",
            "La vivienda seleccionada no es válida o ya no está disponible para añadir a favoritos"
    );
}