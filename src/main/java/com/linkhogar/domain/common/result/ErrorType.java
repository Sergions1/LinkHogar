package com.linkhogar.domain.common.result;

public enum ErrorType {
    FAILURE,        // Error genérico (algo explotó) -> Equivale a HTTP 500
    NOT_FOUND,      // No encontrado -> Equivale a HTTP 404
    VALIDATION,     // Datos incorrectos -> Equivale a HTTP 400
    CONFLICT,       // Regla de negocio rota (ej: email repetido) -> Equivale a HTTP 409
    UNAUTHORIZED    // No tienes permiso -> Equivale a HTTP 401
}
