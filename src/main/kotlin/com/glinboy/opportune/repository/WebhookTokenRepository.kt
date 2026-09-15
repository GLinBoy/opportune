package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.WebhookToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WebhookTokenRepository : JpaRepository<WebhookToken, UUID> {

	fun findByTokenHash(tokenHash: String): Optional<WebhookToken>

	fun findByEndpointIdentifier(endpointIdentifier: String): Optional<WebhookToken>

	fun findAllByProfileIdOrderByCreatedDateDesc(profileId: UUID): List<WebhookToken>
}
