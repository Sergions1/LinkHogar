package com.linkhogar.application.user.getPasswordCode;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.externalServices.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GetPasswordCodeQueryHandler {
    private final UserRepository userRepository;
    private final MailService mailService;

    public void handle(GetPasswordCodeQuery query) {
        User user = userRepository.userByMail(query.mail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(10));

        userRepository.saveUser(user);

        mailService.sendPasswordResetEmail(user.getMail(), code);
    }

}
