package com.linkhogar.application.home.removeMember;

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
public class RemoveMemberFromHomeCommandHandler {

    private final UserRepository userRepository;

    @Transactional
    public Result<Void> handle(RemoveMemberFromHomeCommand command) {
        try {
            Optional<User> targetUserOpt = userRepository.userById(command.memberId());
            if (targetUserOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(command.memberId()));
            }

            User targetUser = targetUserOpt.get();

            if (targetUser.getHomeId() == null || !targetUser.getHomeId().equals(command.homeId())) {
                return Result.failure(HomeErrors.USER_NOT_IN_HOME);
            }

            targetUser.setHomeId(null);
            userRepository.saveUser(targetUser);

            return Result.success(null);

        } catch (Exception e) {
            System.out.println("Error al eliminar integrante del hogar: " + e.getMessage());
            return Result.failure(HomeErrors.REMOVE_MEMBER_FAILED);
        }
    }
}