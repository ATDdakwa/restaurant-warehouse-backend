package com.vozhe.jwt.repository.settings;

import com.vozhe.jwt.models.settings.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
    // Since there will likely be only one GlobalSettings record,
    // we can add a method to find the single instance.
    GlobalSettings findFirstByOrderByIdAsc();
}
