package com.linkhogar.application.user.update;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public record UserUpdateDTO(
        String firstName,
        String lastName,

        @JsonProperty("fecha_Nac")
        LocalDate fecha_Nac,

        String phone,
        Optional<String> role
) {}
