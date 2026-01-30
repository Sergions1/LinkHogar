package com.linkhogar.application.user.delete;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserCommandHandler {
    private final UserRepository userRepository;

    public Result<Void> handle(DeleteUserCommand command){
        if(userRepository.userById(command.userId()).isEmpty()){
            return Result.failure(UserErrors.NotFound(command.userId()));
        }

        userRepository.delete(command.userId());

        return Result.success(null);

    }
}
