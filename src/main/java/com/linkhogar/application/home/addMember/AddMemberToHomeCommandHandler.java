package com.linkhogar.application.home.addMember;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeErrors;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddMemberToHomeCommandHandler {

    private final UserRepository userRepository;

    @Transactional
    public Result<Void> handle(AddMemberToHomeCommand command) {
        try {
            Optional<User> requesterOpt = userRepository.userById(command.requesterId());
            if (requesterOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(command.requesterId()));
            }

            Optional<User> targetUserOpt = userRepository.userByMail(command.email());
            if (targetUserOpt.isEmpty()) {
                return Result.failure(UserErrors.NOT_FOUND_BY_EMAIL);
            }

            User targetUser = targetUserOpt.get();

            //Comprobar si ya pertenece a un hogar
            if (targetUser.getHomeId() != null) {
                if (targetUser.getHomeId().equals(command.homeId())) {
                    return Result.failure(HomeErrors.USER_IN_HOME);
                } else {
                    return Result.failure(HomeErrors.USER_IN_OTHER_HOME);
                }
            }

            // 4. Asignar el hogar y guardar
            targetUser.setHomeId(command.homeId());
            userRepository.saveUser(targetUser);

            return Result.success(null);

        } catch (Exception e) {
            System.out.println("Error al añadir integrante al hogar: " + e.getMessage());
            return Result.failure(HomeErrors.ADD_MEMBER_FAILED);
        }
    }
}