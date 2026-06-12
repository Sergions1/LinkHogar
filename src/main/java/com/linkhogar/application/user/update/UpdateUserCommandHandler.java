package com.linkhogar.application.user.update;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.domain.user.enums.Role;
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

        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());

        if (command.phone() != null && !command.phone().isBlank()) {
            user.setPhone(Long.parseLong(command.phone()));
        } else {
            user.setPhone(null);
        }

        user.setFecha_nac(command.fecha_Nac());

        if(command.role() != null && command.role().isPresent()){
            user.setRole(Role.valueOf(command.role().get()));
        }

        userRepository.saveUser(user);

        return Result.success(null);
    }
}
