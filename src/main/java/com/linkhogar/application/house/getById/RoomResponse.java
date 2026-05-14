package com.linkhogar.application.house.getById;

import com.linkhogar.domain.room.enums.RoomStatus;

import java.util.List;

public record RoomResponse(
        String id,
        String name,
        String description,
        Long price,
        Double size,
        Boolean hasPrivateBath,
        String bedType,
        RoomStatus status,
        TenantProfileResponse currentTenant,
        List<String> images
) {}


