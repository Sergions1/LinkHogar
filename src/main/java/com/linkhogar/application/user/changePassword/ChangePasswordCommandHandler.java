package com.linkhogar.application.user.changePassword;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChangePasswordCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void handle(ChangePasswordCommand command) {
        User user = userRepository.userByMail(command.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));


        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido");
        }

        if (user.getVerificationCodeExpiration() == null || user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código ha expirado");
        }

        user.setPassword(passwordEncoder.encode(command.newPassword()));

        // Invalidamos el código para que no se pueda usar dos veces
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);

        userRepository.saveUser(user);
    }
}
