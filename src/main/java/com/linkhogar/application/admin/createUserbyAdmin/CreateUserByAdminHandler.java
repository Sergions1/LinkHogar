package com.linkhogar.application.admin.createUserbyAdmin;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.domain.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserByAdminHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserByAdminResponse handle(CreateUserByAdminCommand command) {

        if (userRepository.existByMail(command.mail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        String rawPassword = generatePassword();

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .mail(command.mail())
                .phone(command.phone() != null ? Long.parseLong(command.phone()) : null)
                .fecha_nac(command.fechaNac() != null ? command.fechaNac().atStartOfDay() : null)
                .role(Role.valueOf(command.role()))
                .password(passwordEncoder.encode(rawPassword))
                .enabled(true)
                .registerDate(LocalDateTime.now())
                .build();

        userRepository.saveUser(user);

        // TODO: Enviar correo electrónico al usuario con sus credenciales de acceso

        return new CreateUserByAdminResponse(user.getId(), rawPassword);
    }

    private String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;

        java.util.Random random = new java.util.Random();
        StringBuilder password = new StringBuilder();

        // Garantizamos al menos uno de cada tipo
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Completamos hasta 12 caracteres
        for (int i = 4; i < 12; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        // Mezclamos para que no siempre empiece igual
        java.util.List<Character> chars = new java.util.ArrayList<>();
        for (char c : password.toString().toCharArray()) chars.add(c);
        java.util.Collections.shuffle(chars, random);

        StringBuilder result = new StringBuilder();
        for (char c : chars) result.append(c);
        return result.toString();
    }
}
