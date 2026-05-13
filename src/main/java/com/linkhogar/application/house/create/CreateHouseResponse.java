package com.linkhogar.application.house.create;

import java.util.Map;
import java.util.UUID;

public record CreateHouseResponse(
        UUID id,
        Map<String, UUID> rooms
) {}
