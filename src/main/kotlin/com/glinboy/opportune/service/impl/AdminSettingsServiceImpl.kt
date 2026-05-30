package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.SystemSettingsDTO
import com.glinboy.opportune.entity.SystemSettings
import com.glinboy.opportune.repository.SystemSettingsRepository
import com.glinboy.opportune.service.AdminSettingsService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class AdminSettingsServiceImpl(
    private val systemSettingsRepository: SystemSettingsRepository,
) : AdminSettingsService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getSettings(): SystemSettingsDTO {
        val settings = loadOrDefault()
        return toDTO(settings)
    }

    @Transactional
    override fun updateSettings(dto: SystemSettingsDTO): SystemSettingsDTO {
        val current = loadOrDefault()
        val updated = current.copy(
            registrationEnabled = dto.registrationEnabled ?: current.registrationEnabled,
            aiScoringEnabled = dto.aiScoringEnabled ?: current.aiScoringEnabled,
            emailNotificationsEnabled = dto.emailNotificationsEnabled ?: current.emailNotificationsEnabled,
            maintenanceMode = dto.maintenanceMode ?: current.maintenanceMode,
            maxApplicationsPerUser = dto.maxApplicationsPerUser ?: current.maxApplicationsPerUser,
            maxAiRequestsPerDay = dto.maxAiRequestsPerDay ?: current.maxAiRequestsPerDay,
            lastModifiedDate = Instant.now(),
        )
        log.info("Admin updated system settings")
        return toDTO(systemSettingsRepository.save(updated))
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun loadOrDefault(): SystemSettings =
        systemSettingsRepository.findById(SystemSettings.SETTINGS_ID)
            .orElseGet {
                log.warn("System settings row not found — creating defaults")
                systemSettingsRepository.save(SystemSettings())
            }

    private fun toDTO(s: SystemSettings) = SystemSettingsDTO(
        registrationEnabled = s.registrationEnabled,
        aiScoringEnabled = s.aiScoringEnabled,
        emailNotificationsEnabled = s.emailNotificationsEnabled,
        maintenanceMode = s.maintenanceMode,
        maxApplicationsPerUser = s.maxApplicationsPerUser,
        maxAiRequestsPerDay = s.maxAiRequestsPerDay,
    )
}
