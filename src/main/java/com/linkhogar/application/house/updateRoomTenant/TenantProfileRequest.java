package com.linkhogar.application.house.updateRoomTenant;

import com.linkhogar.domain.room.enums.Occupation;
import com.linkhogar.domain.user.enums.Gender;

public record TenantProfileRequest(
        Gender gender,
        String ageRange,
        Occupation occupation,
        String description,
        Boolean isSmoker,
        Boolean hasPets
) {}
