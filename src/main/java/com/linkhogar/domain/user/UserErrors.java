package com.linkhogar.domain.user;

import com.linkhogar.domain.common.result.Error;
import java.util.UUID;


public class UserErrors {
    private UserErrors(){}

    public static Error NotFound(UUID userId) {
        return Error.notFound(
                "Users.NotFound",
                "El usuario con el Id = '" + userId + "' no fue encontrado"
        );
    }

    public static final Error NOT_FOUND_BY_EMAIL = Error.notFound(
            "Users.NotFoundByEmail",
            "El usuario con el email especificado no fue encontrado"
    );

    public static final Error EMAIL_NOT_UNIQUE = Error.conflict(
            "Users.EmailNotUnique",
            "El email proporcionado ya está registrado en el sistema"
    );

    public static final Error UNAUTHORIZED = Error.unauthorized(
            "Users.Unauthorized",
            "No tienes permisos para realizar esta acción"
    );

    public static final Error NOT_ENABLED = Error.unauthorized(
            "Users.NotEnabled",
            "La cuenta de usuario no está activa."
    );

    public static final Error invalidPassword(){
        return Error.notFound(
                "Users.InvalidPassword",
                "La contraseña es incorrecta"
        );
    }


}
