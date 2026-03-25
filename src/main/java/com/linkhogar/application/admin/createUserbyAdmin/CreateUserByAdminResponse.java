package com.linkhogar.application.admin.createUserbyAdmin;

import java.util.UUID;

public record CreateUserByAdminResponse(
        UUID id,
        String generatedPassword // solo se devuelve en esta respuesta
) {}