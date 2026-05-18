package com.linkhogar.application.settings.updateImage;

import org.springframework.web.multipart.MultipartFile;

public record UpdateSettingImageCommand(
        String settingName,
        MultipartFile file
) {
}
