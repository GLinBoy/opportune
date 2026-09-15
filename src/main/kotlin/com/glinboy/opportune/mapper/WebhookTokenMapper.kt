package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.WebhookTokenDTO
import com.glinboy.opportune.dto.WebhookTokenGenerationDTO
import com.glinboy.opportune.entity.WebhookToken
import org.springframework.stereotype.Component

@Component
class WebhookTokenMapper : GenericMapper<WebhookTokenDTO, WebhookToken> {

	override fun createEntity(dto: WebhookTokenDTO): WebhookToken {
		throw UnsupportedOperationException("Webhook token entities are created from generated values, not from a DTO")
	}

	override fun updateEntity(dto: WebhookTokenDTO, entity: WebhookToken): WebhookToken {
		throw UnsupportedOperationException("Webhook token entities are updated through dedicated service operations")
	}

	/**
	 * Maps a persisted token to its API representation. The token hash is intentionally omitted;
	 * the raw token value is never recoverable from the database.
	 */
	override fun toDto(entity: WebhookToken): WebhookTokenDTO {
		return WebhookTokenDTO(
			id = entity.id,
			endpointIdentifier = entity.endpointIdentifier,
			status = entity.status,
			createdAt = entity.createdDate,
			revokedAt = entity.revokedAt,
			lastUsedAt = entity.lastUsedAt
		)
	}

	/**
	 * Builds the one-time generation payload. [rawToken] is returned to the caller exactly once and
	 * must never be persisted or logged.
	 */
	fun toGenerationDto(entity: WebhookToken, rawToken: String): WebhookTokenGenerationDTO {
		return WebhookTokenGenerationDTO(
			dto = toDto(entity),
			token = rawToken
		)
	}
}
