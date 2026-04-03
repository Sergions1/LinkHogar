package com.linkhogar.infrastructure.persistence.settings;

import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppSettingsRepositoryImpl implements AppSettingsRepository {
    private final JpaAppSettingsRepository jpaAppSettingsRepository;

    @Override
    public Optional<AppSettings> findById(String key) {
        return jpaAppSettingsRepository.findById(key);
    }

    @Override
    public AppSettings save(AppSettings setting) {
        return jpaAppSettingsRepository.save(setting);
    }

    @Override
    public List<AppSettings> findAll() {
       return  jpaAppSettingsRepository.findAll();
    }
}
