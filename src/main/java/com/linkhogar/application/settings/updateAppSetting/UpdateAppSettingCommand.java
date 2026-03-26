package com.linkhogar.application.settings.updateAppSetting;

public record UpdateAppSettingCommand(
        String name,
        String value,
        String description
) {}