package com.glinboy.opportune.web.rest

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.Session
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.enums.SessionStatus
import com.glinboy.opportune.enums.WebhookTokenStatus
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.repository.SessionRepository
import com.glinboy.opportune.repository.WebhookTokenRepository
import com.glinboy.opportune.security.TokenHashUtils
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import java.time.Instant
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WebhookTokenResourceTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	private val json = JsonMapper.builder().build()

	@Autowired
	private lateinit var profileRepository: ProfileRepository

	@Autowired
	private lateinit var sessionRepository: SessionRepository

	@Autowired
	private lateinit var webhookTokenRepository: WebhookTokenRepository

	@Autowired
	private lateinit var jwtEncoder: JwtEncoder

	private lateinit var ownerJwt: String
	private lateinit var otherJwt: String

	@BeforeEach
	fun setUp() {
		ownerJwt = createUserWithSessionJwt()
		otherJwt = createUserWithSessionJwt()
	}

	private fun createUserWithSessionJwt(): String {
		val profile = profileRepository.saveAndFlush(
			Profile(
				email = "webhook-api-${UUID.randomUUID()}@test.com",
				forename = "Webhook",
				surname = "Api",
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

		return jwtEncoder.encode(
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
	}

	private fun generateToken(jwt: String): String =
		mockMvc.perform(
			post("/api/webhook-tokens").header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
		).andExpect(status().isCreated)
			.andReturn().response.contentAsString

	@Test
	fun `generate returns 201 with the raw token once and stores only the hash`() {
		val body = json.readTree(generateToken(ownerJwt))

		assertTrue(body.get("token").asString().startsWith("opw_"))
		val endpointIdentifier = body.get("dto").get("endpointIdentifier").asString()
		assertEquals(WebhookTokenStatus.ACTIVE.name, body.get("dto").get("status").asString())

		val stored = webhookTokenRepository.findByEndpointIdentifier(endpointIdentifier).orElseThrow()
		assertEquals(TokenHashUtils.sha256Hex(body.get("token").asString()), stored.tokenHash)
		assertNotEquals(body.get("token").asString(), stored.tokenHash)
	}

	@Test
	fun `list returns only the current user's tokens and never the raw token`() {
		val ownerBody = json.readTree(generateToken(ownerJwt))
		val ownerEndpoint = ownerBody.get("dto").get("endpointIdentifier").asString()
		generateToken(otherJwt)

		mockMvc.perform(
			get("/api/webhook-tokens").header(HttpHeaders.AUTHORIZATION, "Bearer $ownerJwt")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].endpointIdentifier").value(ownerEndpoint))
			.andExpect(jsonPath("$[0].token").doesNotExist())
			.andExpect(jsonPath("$[0].tokenHash").doesNotExist())
	}

	@Test
	fun `revoke returns 204 and marks the token revoked`() {
		val body = json.readTree(generateToken(ownerJwt))
		val tokenId = body.get("dto").get("id").asString()

		mockMvc.perform(
			delete("/api/webhook-tokens/$tokenId").header(HttpHeaders.AUTHORIZATION, "Bearer $ownerJwt")
		).andExpect(status().isNoContent)

		assertEquals(
			WebhookTokenStatus.REVOKED,
			webhookTokenRepository.findById(UUID.fromString(tokenId)).orElseThrow().status
		)

		mockMvc.perform(
			get("/api/webhook-tokens").header(HttpHeaders.AUTHORIZATION, "Bearer $ownerJwt")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$[0].status").value(WebhookTokenStatus.REVOKED.name))
	}

	@Test
	fun `revoke of another user's token is forbidden`() {
		val body = json.readTree(generateToken(ownerJwt))
		val tokenId = body.get("dto").get("id").asString()

		mockMvc.perform(
			delete("/api/webhook-tokens/$tokenId").header(HttpHeaders.AUTHORIZATION, "Bearer $otherJwt")
		).andExpect(status().isForbidden)

		assertEquals(
			WebhookTokenStatus.ACTIVE,
			webhookTokenRepository.findById(UUID.fromString(tokenId)).orElseThrow().status
		)
	}

	@Test
	fun `revoke of an unknown token returns 404`() {
		mockMvc.perform(
			delete("/api/webhook-tokens/${UUID.randomUUID()}")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $ownerJwt")
		).andExpect(status().isNotFound)
	}

	@Test
	fun `unauthenticated requests are rejected with 401`() {
		mockMvc.perform(post("/api/webhook-tokens")).andExpect(status().isUnauthorized)
		mockMvc.perform(get("/api/webhook-tokens")).andExpect(status().isUnauthorized)
		mockMvc.perform(
			delete("/api/webhook-tokens/${UUID.randomUUID()}").contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isUnauthorized)
	}
}
