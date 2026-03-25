package com.linkhogar.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void saveUser(User user);
    Optional<User> userById(UUID userId);
    Optional<User> userByMail(String mail);
    boolean existByMail(String mail);
    void delete(UUID userId);
    Page<User> findAllFiltered(String search, String role, Boolean enabled, Pageable pageable);
}
