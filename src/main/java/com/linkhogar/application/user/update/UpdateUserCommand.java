package com.linkhogar.application.user.update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String firstName,
        String lastName,
        LocalDate fecha_Nac,
        String phone,
        Optional<String> role
) {}

