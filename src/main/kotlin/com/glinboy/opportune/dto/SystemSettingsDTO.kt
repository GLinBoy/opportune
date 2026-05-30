package com.glinboy.opportune.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * Used for both GET (response) and PATCH (request) of /api/admin/settings.
 * All fields are nullable so a PATCH can omit fields it does not change.
 */
data class SystemSettingsDTO(

    @field:NotNull(message = "registrationEnabled must not be null")
    val registrationEnabled: Boolean? = null,

    @field:NotNull(message = "aiScoringEnabled must not be null")
    val aiScoringEnabled: Boolean? = null,

    @field:NotNull(message = "emailNotificationsEnabled must not be null")
    val emailNotificationsEnabled: Boolean? = null,

    @field:NotNull(message = "maintenanceMode must not be null")
    val maintenanceMode: Boolean? = null,

    @field:NotNull(message = "maxApplicationsPerUser must not be null")
    @field:Min(value = 1, message = "maxApplicationsPerUser must be at least 1")
    @field:Max(value = 100_000, message = "maxApplicationsPerUser must not exceed 100000")
    val maxApplicationsPerUser: Int? = null,

    @field:NotNull(message = "maxAiRequestsPerDay must not be null")
    @field:Min(value = 1, message = "maxAiRequestsPerDay must be at least 1")
    @field:Max(value = 1_000_000, message = "maxAiRequestsPerDay must not exceed 1000000")
    val maxAiRequestsPerDay: Int? = null,
)
