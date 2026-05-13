package com.linkhogar.application.house.Image.deleteRoomImage;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.UUID;

public record DeleteRoomImageCommand(
        UUID houseId,
        UUID roomId,
        String imageUrl,
        UUID userId,
        Collection<? extends GrantedAuthority> authorities
) {}
