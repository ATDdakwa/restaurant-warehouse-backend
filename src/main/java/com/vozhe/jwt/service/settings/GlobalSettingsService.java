package com.vozhe.jwt.service.settings;


import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.repository.settings.GlobalSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettings getGlobalSettings() {
        GlobalSettings settings = globalSettingsRepository.findFirstByOrderByIdAsc();
        if (settings == null) {
            settings = new GlobalSettings();
            settings.setMeatCosts(new HashMap<>());
        }
        return settings;
    }

    public GlobalSettings saveGlobalSettings(GlobalSettings globalSettings) {
        GlobalSettings existingSettings = globalSettingsRepository.findFirstByOrderByIdAsc();
        if (existingSettings != null) {
            existingSettings.setMeatCosts(globalSettings.getMeatCosts());  // Update existing entity
            return globalSettingsRepository.save(existingSettings);
        } else {
            return globalSettingsRepository.save(globalSettings);  // Create new
        }
    }

    // Helper method to get cost for a specific meat type
    public Double getCostPerKg(Long meatTypeId) {
        GlobalSettings settings = getGlobalSettings();
        return settings.getMeatCosts().getOrDefault(meatTypeId, 0.0);
    }
}