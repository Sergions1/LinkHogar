package com.linkhogar.application.user.update;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String firstName,
        String lastName,
        LocalDateTime fecha_Nac
) {}

