package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.SessionStatus
import java.time.Instant
import java.util.*

data class SessionDTO(
	val refreshTokenId: UUID? = null,
	val accessTokenId: UUID? = null,
	val accessTokenExpiration: Instant? = null,
	val refreshTokenExpiration: Instant? = null,
	val status: SessionStatus? = null,
	val lastActiveAt: Instant? = null,
	val clientAgent: String? = null,
	val clientIp: String? = null,
	val clientGeo: String? = null,
	val deviceId: String? = null,
	val deviceType: String? = null,
	val os: String? = null,
	val browser: String? = null,
	val isMobile: Boolean = false,
	val loginAt: Instant? = null,
	val revokedAt: Instant? = null,
	val revocationReason: RevocationReason? = null,
	val lastRefreshedAt: Instant? = null,
	val profileId: UUID? = null
)

