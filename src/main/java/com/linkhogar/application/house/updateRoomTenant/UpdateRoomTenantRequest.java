package com.linkhogar.application.house.updateRoomTenant;

import com.linkhogar.domain.room.enums.RoomStatus;

public record UpdateRoomTenantRequest(
        RoomStatus status,
        TenantProfileRequest tenant
) {}
