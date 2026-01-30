package com.linkhogar.application.user.update;

import java.time.LocalDateTime;

public record UserUpdateDTO(
        String firstName,
        String lastName,
        LocalDateTime fecha_Nac
){}
