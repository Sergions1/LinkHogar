package com.linkhogar.application.house.getById;

import com.linkhogar.domain.room.enums.RoomStatus;

public record RoomResponse(
        String id,
        String name,
        Long price,
        Double size,
        Boolean hasPrivateBath,
        String bedType,
        RoomStatus status,
        TenantProfileResponse currentTenant
) {}
