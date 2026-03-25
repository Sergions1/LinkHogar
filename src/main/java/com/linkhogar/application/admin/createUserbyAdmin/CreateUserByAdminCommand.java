package com.linkhogar.application.admin.createUserbyAdmin;

import java.time.LocalDate;

public record CreateUserByAdminCommand(
        String firstName,
        String lastName,
        String mail,
        String phone,
        LocalDate fechaNac,
        String role
) {}