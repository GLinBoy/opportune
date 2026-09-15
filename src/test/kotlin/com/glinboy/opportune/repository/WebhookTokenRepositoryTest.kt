package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.WebhookToken
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.enums.WebhookTokenStatus
import com.glinboy.opportune.security.TokenHashUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WebhookTokenRepositoryTest {

	@Autowired
	private lateinit var webhookTokenRepository: WebhookTokenRepository

	@Autowired
	private lateinit var profileRepository: ProfileRepository

	private fun newProfile(): Profile = profileRepository.save(
		Profile(
			email = "webhook-${UUID.randomUUID()}@test.com",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)
	)

	@Test
	fun `persists a token and looks it up by hash, endpoint identifier and profile`() {
		val profile = newProfile()
		val endpointIdentifier = UUID.randomUUID().toString()
		val tokenHash = TokenHashUtils.sha256Hex("opw_${UUID.randomUUID()}")

		val saved = webhookTokenRepository.save(
			WebhookToken(
				endpointIdentifier = endpointIdentifier,
				tokenHash = tokenHash,
				profile = profile
			)
		)

		assertNotNull(saved.id)
		assertEquals(WebhookTokenStatus.ACTIVE, saved.status)
		assertEquals(tokenHash, webhookTokenRepository.findByTokenHash(tokenHash).orElseThrow().tokenHash)
		assertEquals(endpointIdentifier, webhookTokenRepository.findByEndpointIdentifier(endpointIdentifier).orElseThrow().endpointIdentifier)
		assertEquals(1, webhookTokenRepository.findAllByProfileIdOrderByCreatedDateDesc(profile.id!!).size)
	}

	@Test
	fun `rejects a duplicate token hash`() {
		val profile = newProfile()
		val tokenHash = TokenHashUtils.sha256Hex("opw_${UUID.randomUUID()}")

		webhookTokenRepository.saveAndFlush(
			WebhookToken(endpointIdentifier = UUID.randomUUID().toString(), tokenHash = tokenHash, profile = profile)
		)

		assertThrows(DataIntegrityViolationException::class.java) {
			webhookTokenRepository.saveAndFlush(
				WebhookToken(endpointIdentifier = UUID.randomUUID().toString(), tokenHash = tokenHash, profile = profile)
			)
		}
	}

	@Test
	fun `rejects a duplicate endpoint identifier`() {
		val profile = newProfile()
		val endpointIdentifier = UUID.randomUUID().toString()

		webhookTokenRepository.saveAndFlush(
			WebhookToken(
				endpointIdentifier = endpointIdentifier,
				tokenHash = TokenHashUtils.sha256Hex("opw_${UUID.randomUUID()}"),
				profile = profile
			)
		)

		assertThrows(DataIntegrityViolationException::class.java) {
			webhookTokenRepository.saveAndFlush(
				WebhookToken(
					endpointIdentifier = endpointIdentifier,
					tokenHash = TokenHashUtils.sha256Hex("opw_${UUID.randomUUID()}"),
					profile = profile
				)
			)
		}
	}
}
