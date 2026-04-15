package com.linkhogar.application.house.Image.deleteImage;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record DeleteHouseImageCommand (
        UUID houseId,
        String imageUrl,
        UUID userId,
        Collection<? extends GrantedAuthority> authorities
) {}
