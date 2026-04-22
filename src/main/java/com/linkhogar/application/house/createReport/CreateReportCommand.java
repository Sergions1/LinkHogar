package com.linkhogar.application.house.createReport;

import java.util.UUID;

public record CreateReportCommand (
    UUID houseId,
    UUID userId,
    String reason,
    String description
){}