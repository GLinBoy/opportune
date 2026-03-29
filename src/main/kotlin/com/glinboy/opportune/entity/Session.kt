package com.glinboy.opportune.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.SessionStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "session")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Session(

	@Id
	@Column(name = "refresh_token_id", nullable = false, updatable = false)
	val refreshTokenId: UUID? = null,

	@Column(name = "access_token_id", nullable = false, unique = true)
	val accessTokenId: UUID? = null,

	@Column(name = "access_token_expiration", nullable = false)
	val accessTokenExpiration: Instant? = null,

	@Column(name = "refresh_token_expiration", nullable = false)
	val refreshTokenExpiration: Instant? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	val status: SessionStatus = SessionStatus.ACTIVE,

	@Column(name = "last_active_at", nullable = false)
	val lastActiveAt: Instant = Instant.now(),

	@Column(name = "client_agent", nullable = false)
	val clientAgent: String? = null,

	@Column(name = "client_ip", nullable = false)
	val clientIp: String? = null,

	@Column(name = "client_geo")
	val clientGeo: String? = null,

	@Column(name = "device_id")
	val deviceId: String? = null,

	@Column(name = "device_type")
	val deviceType: String? = null,

	@Column(name = "os")
	val os: String? = null,

	@Column(name = "browser")
	val browser: String? = null,

	@Column(name = "is_mobile", nullable = false)
	val isMobile: Boolean = false,

	@Column(name = "login_at", nullable = false)
	val loginAt: Instant = Instant.now(),

	@Column(name = "revoked_at")
	val revokedAt: Instant? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "revocation_reason")
	val revocationReason: RevocationReason? = null,

	@Column(name = "last_refreshed_at")
	val lastRefreshedAt: Instant? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null

) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Session) return false
		return refreshTokenId != null && refreshTokenId == other.refreshTokenId
	}

	override fun hashCode(): Int = refreshTokenId?.hashCode() ?: 0
}

