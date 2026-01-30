package com.linkhogar.application.user.update;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserCommandHandler {
    private final UserRepository userRepository;

    public Result<Void> handle (UpdateUserCommand command){
        var optionalUser = userRepository.userById(command.userId());
        if(optionalUser.isEmpty()){
            return Result.failure(UserErrors.NotFound(command.userId()));
        }

        User user = optionalUser.get();

        user.updateUser(command.firstName(), command.lastName(), command.fecha_Nac());

        userRepository.saveUser(user);

        return Result.success(null);
    }
}
