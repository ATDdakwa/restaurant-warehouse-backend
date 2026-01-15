package com.vozhe.jwt.service.settings;

import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.repository.settings.GlobalSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettings getGlobalSettings() {
        // Assuming there's only one GlobalSettings record
        return globalSettingsRepository.findFirstByOrderByIdAsc();
    }

    public GlobalSettings saveGlobalSettings(GlobalSettings globalSettings) {
        // Ensure we only ever have one settings record
        GlobalSettings existingSettings = getGlobalSettings();
        if (existingSettings != null) {
            globalSettings.setId(existingSettings.getId());
        }
        return globalSettingsRepository.save(globalSettings);
    }
}
