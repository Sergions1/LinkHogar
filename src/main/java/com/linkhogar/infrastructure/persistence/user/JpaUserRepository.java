package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMail(String mail);
    boolean existsByMail(String mail);

    @Query("""
        SELECT u FROM User u
        WHERE (:search IS NULL OR
               LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.mail)      LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:role IS NULL OR CAST(u.role AS string) = :role)
          AND (:enabled IS NULL OR u.enabled = :enabled)
        """)
    Page<User> findAllFiltered(
            @Param("search") String search,
            @Param("role") String role,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
