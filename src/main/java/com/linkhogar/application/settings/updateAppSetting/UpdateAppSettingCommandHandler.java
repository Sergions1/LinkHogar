package com.linkhogar.application.settings.updateAppSetting;

import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAppSettingCommandHandler {

    private final AppSettingsRepository settingRepository;

    public void handle(UpdateAppSettingCommand command) {
        AppSettings setting = settingRepository.findById(command.name()).orElse(new AppSettings());
        setting.setName(command.name());
        setting.setValue(command.value());

        if (command.description() != null) {
            setting.setDescription(command.description());
        }

        settingRepository.save(setting);
    }
}
