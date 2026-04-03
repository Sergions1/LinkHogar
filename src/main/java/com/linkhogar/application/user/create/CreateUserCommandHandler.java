package com.linkhogar.application.user.create;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.*;
import com.linkhogar.domain.user.enums.Role;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final MailService mailService;


    public void handle(CreateUserCommand command) {
        if(userRepository.existByMail(command.getMail())){
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .mail(command.getMail())
                .password(passwordEncoder.encode(command.getPassword()))
                .role(Role.User)
                .fecha_nac(command.getFecha_nac())
                .registerDate(LocalDateTime.now())
                .phone(command.getPhone())
                .enabled(false)
                .build();

        userRepository.saveUser(newUser);

        VerificationToken token = VerificationToken.builder()
                .user(newUser)
                .build();
        tokenRepository.save(token);

        mailService.sendVerificationEmail(newUser.getMail(),  token.getToken());

    }
}
