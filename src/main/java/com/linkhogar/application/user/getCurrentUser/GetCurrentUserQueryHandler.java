package com.linkhogar.application.user.getCurrentUser;

import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCurrentUserQueryHandler {
    private final UserRepository userRepository;

    public UserResponse handle(GetCurrentUserQuery query) {
        UUID userId = UUID.fromString(query.userId());
        return UserResponse.mapToResponse(userRepository.userById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }
}