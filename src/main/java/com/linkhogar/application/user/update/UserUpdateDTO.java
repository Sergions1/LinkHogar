package com.linkhogar.application.user.update;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserUpdateDTO(
        String firstName,
        String lastName,
        LocalDate fecha_Nac,
        String phone
) {}
