package com.linkhogar.application.user.getById;

import com.linkhogar.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse (
    UUID id,
    String firstName,
    String lastName,
    String mail,
    LocalDate fechaNac,
    LocalDateTime creationDate,
    String phone,
    String role,
    Boolean enabled,
    String avatarUrl
){

    public static  UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMail(),
                user.getFecha_nac(),
                user.getRegisterDate(),
                user.getPhone() != null ? user.getPhone().toString() : null,
                user.getRole() != null ? user.getRole().toString() : null,
                user.isEnabled(),
                user.getAvatarUrl()
        );
    }
}
