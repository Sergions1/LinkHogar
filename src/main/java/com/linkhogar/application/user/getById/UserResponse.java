package com.linkhogar.application.user.getById;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse (
    UUID id,
    String firstName,
    String lastName,
    String mail,
    LocalDateTime fechaNac,
    LocalDateTime creationDate
){}
