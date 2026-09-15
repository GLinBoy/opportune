package com.glinboy.opportune.service.impl

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.WebhookToken
import com.glinboy.opportune.enums.WebhookTokenStatus
import com.glinboy.opportune.mapper.WebhookTokenMapper
import com.glinboy.opportune.repository.WebhookTokenRepository
import com.glinboy.opportune.security.TokenHashUtils
import com.glinboy.opportune.util.WebhookTokenGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

@ExtendWith(MockitoExtension::class)
class WebhookTokenServiceImplTest {

	@Mock
	private lateinit var repository: WebhookTokenRepository

	private val mapper = WebhookTokenMapper()
	private lateinit var service: WebhookTokenServiceImpl

	private val ownerProfileId: UUID = UUID.randomUUID()
	private val otherProfileId: UUID = UUID.randomUUID()

	@BeforeEach
	fun setUp() {
		service = WebhookTokenServiceImpl(repository, mapper)
	}

	private fun tokenEntity(
		id: UUID = UUID.randomUUID(),
		profileId: UUID = ownerProfileId,
		status: WebhookTokenStatus = WebhookTokenStatus.ACTIVE,
		endpointIdentifier: String = UUID.randomUUID().toString(),
		tokenHash: String = TokenHashUtils.sha256Hex(UUID.randomUUID().toString())
	) = WebhookToken(
		id = id,
		endpointIdentifier = endpointIdentifier,
		tokenHash = tokenHash,
		status = status,
		profile = Profile(id = profileId)
	)

	@Test
	fun `generate persists the hash only and returns the raw token once`() {
		`when`(repository.save(any(WebhookToken::class.java))).thenAnswer { it.arguments[0] as WebhookToken }

		val result = service.generate(ownerProfileId)

		val captor = ArgumentCaptor.forClass(WebhookToken::class.java)
		verify(repository).save(captor.capture())
		val saved = captor.value

		assertTrue(result.token.startsWith(WebhookTokenGenerator.TOKEN_PREFIX))
		assertEquals(TokenHashUtils.sha256Hex(result.token), saved.tokenHash)
		assertNotEquals(result.token, saved.tokenHash)
		assertEquals(WebhookTokenStatus.ACTIVE, saved.status)
		assertEquals(ownerProfileId, saved.profile?.id)
		assertNotNull(saved.endpointIdentifier)
		assertEquals(saved.endpointIdentifier, result.dto.endpointIdentifier)
		assertEquals(saved.createdDate, result.dto.createdAt)
	}

	@Test
	fun `generate produces distinct endpoint identifiers and token hashes`() {
		`when`(repository.save(any(WebhookToken::class.java))).thenAnswer { it.arguments[0] as WebhookToken }

		val first = service.generate(ownerProfileId)
		val second = service.generate(ownerProfileId)

		assertNotEquals(first.dto.endpointIdentifier, second.dto.endpointIdentifier)
		assertNotEquals(TokenHashUtils.sha256Hex(first.token), TokenHashUtils.sha256Hex(second.token))
	}

	@Test
	fun `listByProfile maps every token without exposing the hash`() {
		val endpointIdentifier = UUID.randomUUID().toString()
		`when`(repository.findAllByProfileIdOrderByCreatedDateDesc(ownerProfileId))
			.thenReturn(listOf(tokenEntity(endpointIdentifier = endpointIdentifier)))

		val result = service.listByProfile(ownerProfileId)

		assertEquals(1, result.size)
		assertEquals(endpointIdentifier, result[0].endpointIdentifier)
		assertEquals(WebhookTokenStatus.ACTIVE, result[0].status)
		verify(repository).findAllByProfileIdOrderByCreatedDateDesc(ownerProfileId)
	}

	@Test
	fun `revoke marks an active owned token as revoked`() {
		val entity = tokenEntity()
		`when`(repository.findById(entity.id!!)).thenReturn(Optional.of(entity))
		`when`(repository.save(any(WebhookToken::class.java))).thenAnswer { it.arguments[0] as WebhookToken }

		service.revoke(entity.id!!, ownerProfileId)

		val captor = ArgumentCaptor.forClass(WebhookToken::class.java)
		verify(repository).save(captor.capture())
		assertEquals(WebhookTokenStatus.REVOKED, captor.value.status)
		assertNotNull(captor.value.revokedAt)
	}

	@Test
	fun `revoke rejects a token owned by another profile`() {
		val entity = tokenEntity(profileId = otherProfileId)
		`when`(repository.findById(entity.id!!)).thenReturn(Optional.of(entity))

		val exception = assertThrows(ResponseStatusException::class.java) {
			service.revoke(entity.id!!, ownerProfileId)
		}

		assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
		verify(repository, never()).save(any(WebhookToken::class.java))
	}

	@Test
	fun `revoke rejects an already revoked token`() {
		val entity = tokenEntity(status = WebhookTokenStatus.REVOKED)
		`when`(repository.findById(entity.id!!)).thenReturn(Optional.of(entity))

		val exception = assertThrows(ResponseStatusException::class.java) {
			service.revoke(entity.id!!, ownerProfileId)
		}

		assertEquals(HttpStatus.CONFLICT, exception.statusCode)
		verify(repository, never()).save(any(WebhookToken::class.java))
	}

	@Test
	fun `revoke rejects an unknown token`() {
		val tokenId = UUID.randomUUID()
		`when`(repository.findById(tokenId)).thenReturn(Optional.empty())

		val exception = assertThrows(ResponseStatusException::class.java) {
			service.revoke(tokenId, ownerProfileId)
		}

		assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
	}

	@Test
	fun `findActiveTokenByHashAndEndpoint returns a matching active token`() {
		val endpointIdentifier = UUID.randomUUID().toString()
		val tokenHash = TokenHashUtils.sha256Hex("opw_raw")
		val entity = tokenEntity(endpointIdentifier = endpointIdentifier, tokenHash = tokenHash)
		`when`(repository.findByTokenHash(tokenHash)).thenReturn(Optional.of(entity))

		val found = service.findActiveTokenByHashAndEndpoint(tokenHash, endpointIdentifier)

		assertNotNull(found)
		assertEquals(entity.id, found!!.id)
	}

	@Test
	fun `findActiveTokenByHashAndEndpoint rejects an endpoint mismatch`() {
		val tokenHash = TokenHashUtils.sha256Hex("opw_raw")
		`when`(repository.findByTokenHash(tokenHash))
			.thenReturn(Optional.of(tokenEntity(tokenHash = tokenHash, endpointIdentifier = "endpoint-a")))

		assertNull(service.findActiveTokenByHashAndEndpoint(tokenHash, "endpoint-b"))
	}

	@Test
	fun `findActiveTokenByHashAndEndpoint rejects a revoked token`() {
		val endpointIdentifier = UUID.randomUUID().toString()
		val tokenHash = TokenHashUtils.sha256Hex("opw_raw")
		`when`(repository.findByTokenHash(tokenHash)).thenReturn(
			Optional.of(
				tokenEntity(
					tokenHash = tokenHash,
					endpointIdentifier = endpointIdentifier,
					status = WebhookTokenStatus.REVOKED
				)
			)
		)

		assertNull(service.findActiveTokenByHashAndEndpoint(tokenHash, endpointIdentifier))
	}
}
