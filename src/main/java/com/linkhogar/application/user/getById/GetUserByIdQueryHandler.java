package com.linkhogar.application.user.getById;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdQueryHandler {
    private final UserRepository userRepository;

    public Result<UserResponse> handle(GetUserByIdQuery query){
        return userRepository.userById(query.userId())
                .map(user -> Result.success(mapToResponse(user)))
                .orElse(Result.failure(UserErrors.NotFound(query.userId())));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMail(),
                user.getFecha_nac(),
                user.getRegisterDate()
        );
    }
}
