package com.linkhogar.application.user.create;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler {
    private final UserRepository userRepository;

    public Result<UUID> handle(CreateUserCommand command) {
        if(userRepository.userByMail(command.getMail()).isPresent()){
            return Result.failure(UserErrors.EMAIL_NOT_UNIQUE);
        }

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .mail(command.getMail())
                .password(command.getPassword())
                .fecha_nac(command.getFecha_nac())
                .build();

        userRepository.saveUser(newUser);

        return Result.success(newUser.getId());

    }
}
