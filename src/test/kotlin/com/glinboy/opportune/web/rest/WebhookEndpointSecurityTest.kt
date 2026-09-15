package com.glinboy.opportune.web.rest

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.Session
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.enums.SessionStatus
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.repository.SessionRepository
import com.glinboy.opportune.repository.WebhookTokenRepository
import com.glinboy.opportune.service.WebhookTokenService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WebhookEndpointSecurityTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var profileRepository: ProfileRepository

	@Autowired
	private lateinit var sessionRepository: SessionRepository

	@Autowired
	private lateinit var webhookTokenRepository: WebhookTokenRepository

	@Autowired
	private lateinit var applicationRepository: ApplicationRepository

	@Autowired
	private lateinit var webhookTokenService: WebhookTokenService

	@Autowired
	private lateinit var jwtEncoder: JwtEncoder

	private lateinit var profile: Profile
	private lateinit var sessionJwt: String
	private lateinit var endpointIdentifier: String
	private lateinit var rawToken: String

	@BeforeEach
	fun setUp() {
		profile = profileRepository.saveAndFlush(
			Profile(
				email = "webhook-${UUID.randomUUID()}@test.com",
				forename = "Webhook",
				surname = "Tester",
				password = "hashed",
				status = AccountStatus.ACTIVE,
				roles = setOf(Role.ROLE_USER)
			)
		)

		val accessTokenId = UUID.randomUUID()
		sessionRepository.saveAndFlush(
			Session(
				refreshTokenId = UUID.randomUUID(),
				accessTokenId = accessTokenId,
				accessTokenExpiration = Instant.now().plusSeconds(3600),
				refreshTokenExpiration = Instant.now().plusSeconds(7200),
				status = SessionStatus.ACTIVE,
				lastActiveAt = Instant.now(),
				clientAgent = "test-agent",
				clientIp = "127.0.0.1",
				isMobile = false,
				loginAt = Instant.now(),
				profile = profile
			)
		)
		sessionJwt = jwtEncoder.encode(
			JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS512).type("JWT").build(),
				JwtClaimsSet.builder()
					.subject(profile.id.toString())
					.issuedAt(Instant.now())
					.expiresAt(Instant.now().plusSeconds(3600))
					.id(accessTokenId.toString())
					.claim("roles", listOf(Role.ROLE_USER.name))
					.build()
			)
		).tokenValue

		val generated = webhookTokenService.generate(profile.id!!)
		endpointIdentifier = generated.dto.endpointIdentifier!!
		rawToken = generated.token
	}

	private fun submissionBody(url: String) =
		"""{"url":"$url","html":"<html><head><title>Backend Engineer</title></head><body>Role</body></html>"}"""

	@Test
	fun `session JWT is accepted on the main API`() {
		mockMvc.perform(
			get("/api/applications/list").header(HttpHeaders.AUTHORIZATION, "Bearer $sessionJwt")
		).andExpect(status().isOk)
	}

	@Test
	fun `session JWT is rejected on the webhook endpoint`() {
		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $sessionJwt")
				.contentType(MediaType.APPLICATION_JSON)
				.content(submissionBody("https://example.com/jobs/${UUID.randomUUID()}"))
		).andExpect(status().isUnauthorized)
	}

	@Test
	fun `webhook token is rejected on the main API`() {
		mockMvc.perform(
			get("/api/applications/list").header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
		).andExpect(status().isUnauthorized)
	}

	@Test
	fun `webhook token bound to a different endpoint is rejected`() {
		mockMvc.perform(
			post("/api/webhook/${UUID.randomUUID()}")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(submissionBody("https://example.com/jobs/${UUID.randomUUID()}"))
		).andExpect(status().isUnauthorized)
	}

	@Test
	fun `missing webhook token is rejected`() {
		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.contentType(MediaType.APPLICATION_JSON)
				.content(submissionBody("https://example.com/jobs/${UUID.randomUUID()}"))
		).andExpect(status().isUnauthorized)
	}

	@Test
	fun `invalid payload is rejected with 400`() {
		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"url":""}""")
		).andExpect(status().isBadRequest)
	}

	@Test
	fun `matching webhook token creates an application and touches last used at`() {
		val url = "https://example.com/jobs/${UUID.randomUUID()}"

		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(submissionBody(url))
		).andExpect(status().isCreated)

		val created = applicationRepository.findAll()
			.firstOrNull { it.url == url && it.profile?.id == profile.id }
		assertNotNull(created)
		assertEquals(ApplicationStatus.INITIATED, created!!.status)
		assertTrue(created.rawContent!!.contains("Backend Engineer"))

		assertNotNull(webhookTokenRepository.findByEndpointIdentifier(endpointIdentifier).orElseThrow().lastUsedAt)
	}

	@Test
	fun `duplicate url with a matching webhook token is rejected with 409`() {
		val url = "https://example.com/jobs/${UUID.randomUUID()}"
		val body = submissionBody(url)

		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		).andExpect(status().isCreated)

		mockMvc.perform(
			post("/api/webhook/$endpointIdentifier")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		).andExpect(status().isConflict)
	}
}
