package com.linkhogar.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void saveUser(User user);
    Optional<User> userById(UUID userId);
    Optional<User> userByMail(String mail);
}
