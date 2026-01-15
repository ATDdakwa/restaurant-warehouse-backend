package com.vozhe.jwt.controller.settings;

import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.service.settings.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class GlobalSettingsController {

    private final GlobalSettingsService globalSettingsService;

    @GetMapping
    public ResponseEntity<GlobalSettings> getGlobalSettings() {
        return ResponseEntity.ok(globalSettingsService.getGlobalSettings());
    }

    @PostMapping
    public ResponseEntity<GlobalSettings> saveGlobalSettings(@RequestBody GlobalSettings globalSettings) {
        return ResponseEntity.ok(globalSettingsService.saveGlobalSettings(globalSettings));
    }
}
