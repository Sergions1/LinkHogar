package com.linkhogar.infrastructure.persistence.settings;

import com.linkhogar.domain.settings.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAppSettingsRepository extends JpaRepository<AppSettings, String> {
}