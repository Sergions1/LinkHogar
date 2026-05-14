package com.linkhogar.application.house.updateRoomTenant;

import com.linkhogar.domain.room.enums.RoomStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record UpdateRoomTenantCommand(
        UUID houseId,
        UUID roomId,
        RoomStatus status,
        TenantProfileRequest tenant,
        UUID userId,
        Collection<? extends GrantedAuthority> authorities
) {}
