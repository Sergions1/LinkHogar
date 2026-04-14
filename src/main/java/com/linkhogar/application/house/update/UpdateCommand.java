package com.linkhogar.application.house.update;

import com.linkhogar.application.house.create.CreateHouseCommand;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record UpdateCommand (
        UUID houseId,
        CreateHouseCommand data, // Los datos planos que vienen de Angular
        UUID userId,              // Quién intenta editar
        Collection<? extends GrantedAuthority> authorities // Qué roles tiene
) {}