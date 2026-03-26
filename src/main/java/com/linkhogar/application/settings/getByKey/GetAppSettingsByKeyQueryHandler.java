package com.linkhogar.application.settings.getByKey;

import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAppSettingsByKeyQueryHandler {
    private final AppSettingsRepository settingRepository;

    public String handle(GetAppSettingsByKeyQuery query) {
        return settingRepository.findById(query.name())
                .map(AppSettings::getValue)
                .orElse(query.defaultValue());
    }
}
