package com.linkhogar.application.user.toggleUserEnabled;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToggleUserEnabledHandler {
    private final UserRepository userRepository;

    public void handle(ToggleUserEnabledCommand command) {
        User user = userRepository.userById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled());
        userRepository.saveUser(user);
    }
}