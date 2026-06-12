package com.linkhogar.application.user.login;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.ErrorType;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginCommandHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder pswEncoder;

    public Result<String> handle(UserLoginCommand command){
        User user = userRepository.userByMail(command.mail()).orElse(null);

        if(user == null){
            return Result.failure(UserErrors.NOT_FOUND_BY_EMAIL);
        }

        if(!user.isEnabled()){
            return Result.failure(UserErrors.NOT_ENABLED);
        }

        if (!pswEncoder.matches(command.password(), user.getPassword())){
            return Result.failure(UserErrors.invalidPassword());
        }

        String token = jwtService.generateToken(user);

        return Result.success(token);
    }
}
