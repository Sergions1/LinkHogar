package com.linkhogar.application.house.getById;

import com.linkhogar.domain.room.enums.Occupation;
import com.linkhogar.domain.user.enums.Gender;

public record TenantProfileResponse(
        Gender gender,
        String ageRange,
        Occupation occupation,
        String description,
        Boolean isSmoker,
        Boolean hasPets
) {}