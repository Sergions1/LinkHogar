package com.linkhogar.domain.settings;

import java.util.Optional;

public interface AppSettingsRepository {
    Optional<AppSettings> findById(String key);
    AppSettings save(AppSettings setting);
}
