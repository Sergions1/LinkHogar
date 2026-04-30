package com.linkhogar.application.user.verify;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.domain.user.VerificationToken;
import com.linkhogar.domain.user.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyUserCommandHandler {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handle(VerifyUserCommand command) {

        System.out.println("🔍 Buscando token: " + command.token());
        System.out.println("🔍 Tokens en BD: " + tokenRepository.findAll()
                .stream().map(VerificationToken::getToken).toList());

        VerificationToken verificationToken = tokenRepository.findByToken(command.token())
                .orElseThrow(() -> new IllegalArgumentException("Token no válido o no encontrado"));

        // Añade esto temporalmente
        System.out.println("Token encontrado. User asociado: " + verificationToken.getUser());
        System.out.println("User ID: " + verificationToken.getUser().getId());

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("El token ha expirado. Por favor, solicita uno nuevo.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.saveUser(user);

        // Eliminamos el token para que no se pueda volver a usar
        tokenRepository.delete(verificationToken);
    }
}
