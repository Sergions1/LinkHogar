package com.linkhogar.application.settings.updateImage;

import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UpdateSettingImageCommandHandler {

    private final AppSettingsRepository appSettingsRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    @CacheEvict(value = "appSetings", key = "#command.settingName()")
    public String handle(UpdateSettingImageCommand command) {
        try {
            String secureUrl = cloudinaryService.uploadImage(command.file());

            AppSettings setting = appSettingsRepository.findById(command.settingName())
                    .orElse(AppSettings.builder()
                            .name(command.settingName())
                            .description("Imagen para " + command.settingName())
                            .build());

            if (setting.getValue() != null && setting.getValue().startsWith("http")) {
                String oldPublicId = cloudinaryService.extractPublicId(setting.getValue());
                if (oldPublicId != null) {
                    cloudinaryService.deleteImage(oldPublicId);
                }
            }

            setting.setValue(secureUrl);
            appSettingsRepository.save(setting);

            return secureUrl;

        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen de configuración a Cloudinary", e);
        }
    }
}