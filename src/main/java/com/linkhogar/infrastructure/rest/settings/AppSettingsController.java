package com.linkhogar.infrastructure.rest.settings;

import com.linkhogar.application.settings.getByName.GetAppSettingsByNameQuery;
import com.linkhogar.application.settings.getByName.GetAppSettingsByNameQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AppSettings")
@CrossOrigin
@RequiredArgsConstructor
public class AppSettingsController {

    private final GetAppSettingsByNameQueryHandler getAppSettingsByNameQueryHandler;

    @GetMapping("/{name}")
    public ResponseEntity<String> getSetting(@PathVariable String name) {
        GetAppSettingsByNameQuery query = new GetAppSettingsByNameQuery(name, null);
        String value = getAppSettingsByNameQueryHandler.handle(query);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }
}
