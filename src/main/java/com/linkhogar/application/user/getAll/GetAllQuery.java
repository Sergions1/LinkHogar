package com.linkhogar.application.user.getAll;

public record GetAllQuery(
        int page,
        int size,
        String search,   // busca en nombre, apellido y email
        String role,
        Boolean enabled
) {}
