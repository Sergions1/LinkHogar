package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMail(String mail);
    boolean existsByMail(String mail);
}
