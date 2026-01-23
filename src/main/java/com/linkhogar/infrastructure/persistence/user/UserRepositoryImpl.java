package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void saveUser(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mail(user.getMail())
                .password(user.getPassword())
                .fecha_nac(user.getFecha_nac())
                .creationDate(user.getCreationDate())
                .build();

        jpaUserRepository.save(entity);
    }

    @Override
    public Optional<User> userById(UUID userId) {
        return jpaUserRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public Optional<User> userByMail(String mail) {
        return jpaUserRepository.findByMail(mail).map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .mail(entity.getMail())
                .password(entity.getPassword())
                .fecha_nac(entity.getFecha_nac())
                .creationDate(entity.getCreationDate())
                .build();
    }
}
