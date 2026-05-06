package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void saveUser(User user) {

        jpaUserRepository.save(user);
    }

    @Override
    public Optional<User> userById(UUID userId) {
        return jpaUserRepository.findById(userId);
    }

    @Override
    public Optional<User> userByMail(String mail) {
        return jpaUserRepository.findByMail(mail);
    }

    @Override
    public boolean existByMail(String mail) {
        return jpaUserRepository.existsByMail(mail);
    }

    @Override
    public void delete(UUID userId) {
        jpaUserRepository.deleteById(userId);
    }

    @Override
    public Page<User> findAllFiltered(String search, String role, Boolean enabled, Pageable pageable) {
        return jpaUserRepository.findAllFiltered(search, role, enabled, pageable);
    }

    @Override
    public List<User> findByHome(UUID homeId) {
        return jpaUserRepository.findByHomeId(homeId);
    }

}
