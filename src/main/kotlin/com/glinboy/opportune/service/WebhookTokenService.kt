package com.glinboy.opportune.service

import com.glinboy.opportune.dto.WebhookTokenDTO
import com.glinboy.opportune.dto.WebhookTokenGenerationDTO
import com.glinboy.opportune.entity.WebhookToken
import java.util.*

interface WebhookTokenService {
	fun generate(profileId: UUID): WebhookTokenGenerationDTO
	fun listByProfile(profileId: UUID): List<WebhookTokenDTO>
	fun revoke(tokenId: UUID, profileId: UUID)
	fun findActiveTokenByHashAndEndpoint(tokenHash: String, endpointIdentifier: String): WebhookToken?

	/**
	 * Authenticates an incoming webhook call. The token hash and the endpoint identifier must both
	 * resolve to the same ACTIVE row. On success, the owning profile id is returned and the token's
	 * `lastUsedAt` is refreshed. Returns null when the token is missing, revoked or bound to a
	 * different endpoint.
	 */
	fun authenticate(tokenHash: String, endpointIdentifier: String): UUID?
}
