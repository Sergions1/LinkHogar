package com.linkhogar.application.settings.getByName;

import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAppSettingsByNameQueryHandler {
    private final AppSettingsRepository settingRepository;

    @Cacheable(value = "appSettings", key = "#query.name()")
    public String handle(GetAppSettingsByNameQuery query) {
        return settingRepository.findById(query.name())
                .map(AppSettings::getValue)
                .orElse(query.defaultValue());
    }
}
