package com.linkhogar.application.user.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String mail;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    private LocalDateTime fecha_nac;
}
