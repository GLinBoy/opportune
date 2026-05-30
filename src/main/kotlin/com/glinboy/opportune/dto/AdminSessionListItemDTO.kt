package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.SessionStatus
import java.time.Instant
import java.util.*

data class AdminSessionListItemDTO(
	val refreshTokenId: UUID? = null,
	val status: SessionStatus? = null,
	val lastActiveAt: Instant? = null,
	val clientIp: String? = null,
	val clientGeo: String? = null,
	val os: String? = null,
	val browser: String? = null,
	val isMobile: Boolean = false,
	val loginAt: Instant? = null,
	val revokedAt: Instant? = null,
	val revocationReason: RevocationReason? = null,
	val profileId: UUID? = null,
	val userEmail: String? = null,
	val userForename: String? = null,
	val userSurname: String? = null,
)
