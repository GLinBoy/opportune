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
}
