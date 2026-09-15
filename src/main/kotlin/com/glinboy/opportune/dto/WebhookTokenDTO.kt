package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.WebhookTokenStatus
import java.time.Instant
import java.util.*

data class WebhookTokenDTO(
	val id: UUID? = null,
	val endpointIdentifier: String? = null,
	val status: WebhookTokenStatus? = null,
	val createdAt: Instant? = null,
	val revokedAt: Instant? = null,
	val lastUsedAt: Instant? = null
)
