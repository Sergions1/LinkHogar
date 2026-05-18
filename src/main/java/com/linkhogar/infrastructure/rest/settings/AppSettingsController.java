package com.linkhogar.infrastructure.rest.settings;

import com.linkhogar.application.settings.getAllAppSettingsQuery.GetAllAppSettingsQuery;
import com.linkhogar.application.settings.getByName.GetAppSettingsByNameQuery;
import com.linkhogar.application.settings.getByName.GetAppSettingsByNameQueryHandler;
import com.linkhogar.application.settings.updateImage.UpdateSettingImageCommand;
import com.linkhogar.application.settings.updateImage.UpdateSettingImageCommandHandler;
import com.linkhogar.application.settings.getAll.GetAllAppSettingsQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/AppSettings")
@CrossOrigin
@RequiredArgsConstructor
public class AppSettingsController {

    private final GetAppSettingsByNameQueryHandler getAppSettingsByNameQueryHandler;
    private final UpdateSettingImageCommandHandler updateSettingImageCommandHandler;
    private final GetAllAppSettingsQueryHandler getAllAppSettingsQueryHandler;

    @GetMapping("/{name}")
    public ResponseEntity<String> getSetting(@PathVariable String name) {
        GetAppSettingsByNameQuery query = new GetAppSettingsByNameQuery(name, null);
        String value = getAppSettingsByNameQueryHandler.handle(query);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }

    @PutMapping(value = "/{name}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateSettingImage(
            @PathVariable String name,
            @RequestParam("file") MultipartFile file) {

        UpdateSettingImageCommand command = new UpdateSettingImageCommand(name, file);
        String newUrl = updateSettingImageCommandHandler.handle(command);

        return ResponseEntity.ok(newUrl);
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllSettings() {
        Map<String, String> settings = getAllAppSettingsQueryHandler.handle(new GetAllAppSettingsQuery());
        return ResponseEntity.ok(settings);
    }
}
