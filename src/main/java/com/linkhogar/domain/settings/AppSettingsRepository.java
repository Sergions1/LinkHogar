package com.linkhogar.domain.settings;

import java.util.List;
import java.util.Optional;

public interface AppSettingsRepository {
    Optional<AppSettings> findById(String key);
    AppSettings save(AppSettings setting);
    List<AppSettings> findAll();
}
