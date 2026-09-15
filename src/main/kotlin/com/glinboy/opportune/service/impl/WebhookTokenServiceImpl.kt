package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.WebhookTokenDTO
import com.glinboy.opportune.dto.WebhookTokenGenerationDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.WebhookToken
import com.glinboy.opportune.enums.WebhookTokenStatus
import com.glinboy.opportune.mapper.WebhookTokenMapper
import com.glinboy.opportune.repository.WebhookTokenRepository
import com.glinboy.opportune.security.TokenHashUtils
import com.glinboy.opportune.service.WebhookTokenService
import com.glinboy.opportune.util.WebhookTokenGenerator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
@Transactional(readOnly = true)
class WebhookTokenServiceImpl(
	private val repository: WebhookTokenRepository,
	private val mapper: WebhookTokenMapper
) : WebhookTokenService {

	private val log = LoggerFactory.getLogger(this::class.java)

	@Transactional
	override fun generate(profileId: UUID): WebhookTokenGenerationDTO {
		val rawToken = WebhookTokenGenerator.generateRawToken()
		val webhookToken = WebhookToken(
			endpointIdentifier = WebhookTokenGenerator.generateEndpointIdentifier(),
			tokenHash = TokenHashUtils.sha256Hex(rawToken),
			status = WebhookTokenStatus.ACTIVE,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = Profile(id = profileId)
		)
		val saved = repository.save(webhookToken)
		log.debug("Generated webhook token with ID: {}", saved.id)
		return mapper.toGenerationDto(saved, rawToken)
	}

	override fun listByProfile(profileId: UUID): List<WebhookTokenDTO> =
		repository.findAllByProfileIdOrderByCreatedDateDesc(profileId)
			.map(mapper::toDto)

	@Transactional
	override fun revoke(tokenId: UUID, profileId: UUID) {
		val webhookToken = repository.findById(tokenId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook token not found") }
		if (webhookToken.profile?.id != profileId) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to revoke this webhook token")
		}
		if (webhookToken.status != WebhookTokenStatus.ACTIVE) {
			throw ResponseStatusException(HttpStatus.CONFLICT, "Webhook token is already revoked")
		}
		log.debug("Revoking webhook token with ID: {}", webhookToken.id)
		repository.save(
			webhookToken.copy(
				status = WebhookTokenStatus.REVOKED,
				revokedAt = Instant.now(),
				lastModifiedDate = Instant.now()
			)
		)
	}

	override fun findActiveTokenByHashAndEndpoint(tokenHash: String, endpointIdentifier: String): WebhookToken? =
		repository.findByTokenHash(tokenHash)
			.filter { it.status == WebhookTokenStatus.ACTIVE && it.endpointIdentifier == endpointIdentifier }
			.orElse(null)
}
