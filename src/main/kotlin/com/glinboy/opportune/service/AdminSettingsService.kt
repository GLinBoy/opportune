package com.glinboy.opportune.service

import com.glinboy.opportune.dto.SystemSettingsDTO

interface AdminSettingsService {
    fun getSettings(): SystemSettingsDTO
    fun updateSettings(dto: SystemSettingsDTO): SystemSettingsDTO
}
