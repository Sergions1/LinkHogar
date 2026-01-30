package com.linkhogar.application.user.create;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.domain.user.enums.Role;
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

    public Result<UUID> handle(CreateUserCommand command) {
        if(userRepository.existByMail(command.getMail())){
            return Result.failure(UserErrors.EMAIL_NOT_UNIQUE);
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
                .build();

        userRepository.saveUser(newUser);

        return Result.success(newUser.getId());

    }
}
