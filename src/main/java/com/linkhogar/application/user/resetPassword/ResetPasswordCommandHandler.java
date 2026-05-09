package com.linkhogar.application.user.resetPassword;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void handle(ResetPasswordCommand command) {
        User user = userRepository.userByMail(command.mail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(command.code())) {
            throw new RuntimeException("Código inválido");
        }

        if (user.getVerificationCodeExpiration() == null || user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado");
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);

        String encodedPassword = passwordEncoder.encode(command.newPassword());
        user.setPassword(encodedPassword);

        userRepository.saveUser(user);
    }
}
