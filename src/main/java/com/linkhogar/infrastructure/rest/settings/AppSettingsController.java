package com.linkhogar.infrastructure.rest.settings;

import com.linkhogar.application.settings.getByKey.GetAppSettingsByKeyQuery;
import com.linkhogar.application.settings.getByKey.GetAppSettingsByKeyQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/AppSettings")
@RequiredArgsConstructor
public class AppSettingsController {

    private final GetAppSettingsByKeyQueryHandler getAppSettingsByKeyQueryHandler;

    @GetMapping("/{name}")
    public ResponseEntity<String> getSetting(@PathVariable String name) {
        GetAppSettingsByKeyQuery query = new GetAppSettingsByKeyQuery(name, null);
        String value = getAppSettingsByKeyQueryHandler.handle(query);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }
}
