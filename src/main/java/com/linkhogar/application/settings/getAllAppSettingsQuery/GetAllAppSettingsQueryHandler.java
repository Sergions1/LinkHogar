package com.linkhogar.application.settings.getAll;

import com.linkhogar.application.settings.getAllAppSettingsQuery.GetAllAppSettingsQuery;
import com.linkhogar.domain.settings.AppSettings;
import com.linkhogar.domain.settings.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllAppSettingsQueryHandler {

    private final AppSettingsRepository appSettingsRepository;

    @Transactional(readOnly = true)
    public Map<String, String> handle(GetAllAppSettingsQuery query) {
        List<AppSettings> allSettings = appSettingsRepository.findAll();

        // Convertimos la lista en un diccionario (clave: nombre, valor: url/texto)
        return allSettings.stream()
                .collect(Collectors.toMap(AppSettings::getName, AppSettings::getValue));
    }
}