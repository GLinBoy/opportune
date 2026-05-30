package com.glinboy.opportune.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

/**
 * Singleton settings row.
 * There is always exactly one record with id = SETTINGS_ID.
 */
@Entity
@Table(name = "system_settings")
data class SystemSettings(

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = SETTINGS_ID,

    @Column(name = "registration_enabled", nullable = false)
    val registrationEnabled: Boolean = true,

    @Column(name = "ai_scoring_enabled", nullable = false)
    val aiScoringEnabled: Boolean = true,

    @Column(name = "email_notifications_enabled", nullable = false)
    val emailNotificationsEnabled: Boolean = true,

    @Column(name = "maintenance_mode", nullable = false)
    val maintenanceMode: Boolean = false,

    @Column(name = "max_applications_per_user", nullable = false)
    val maxApplicationsPerUser: Int = 100,

    @Column(name = "max_ai_requests_per_day", nullable = false)
    val maxAiRequestsPerDay: Int = 1000,

    @Column(name = "created_date", updatable = false)
    val createdDate: Instant = Instant.now(),

    @Column(name = "last_modified_date")
    val lastModifiedDate: Instant = Instant.now(),
) {
    companion object {
        val SETTINGS_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}
