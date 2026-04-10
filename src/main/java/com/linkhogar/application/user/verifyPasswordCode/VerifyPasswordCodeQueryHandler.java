package com.linkhogar.application.user.verifyPasswordCode;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyPasswordCodeQueryHandler {
    private final UserRepository userRepository;

    public void handle(VerifyPasswordCodeQuery query){
        User user = userRepository.userByMail(query.mail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(query.code())) {
            throw new RuntimeException("Código inválido");
        }

        if (user.getVerificationCodeExpiration() == null || user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado");
        }
    }
}
