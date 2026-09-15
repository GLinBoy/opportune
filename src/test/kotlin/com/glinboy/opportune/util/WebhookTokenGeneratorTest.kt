package com.glinboy.opportune.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class WebhookTokenGeneratorTest {

	@Test
	fun `generateRawToken uses the opw_ prefix`() {
		assertTrue(WebhookTokenGenerator.generateRawToken().startsWith(WebhookTokenGenerator.TOKEN_PREFIX))
	}

	@Test
	fun `generateRawToken body is unpadded url-safe base64 of 32 random bytes`() {
		val token = WebhookTokenGenerator.generateRawToken()
		val encoded = token.removePrefix(WebhookTokenGenerator.TOKEN_PREFIX)

		assertEquals(32, Base64.getUrlDecoder().decode(encoded).size)
		assertFalse(encoded.contains('+'))
		assertFalse(encoded.contains('/'))
		assertFalse(encoded.contains('='))
	}

	@Test
	fun `generateRawToken produces unique values`() {
		val tokens = (1..1000).map { WebhookTokenGenerator.generateRawToken() }.toSet()
		assertEquals(1000, tokens.size)
	}

	@Test
	fun `generateEndpointIdentifier returns unique valid UUID strings`() {
		val identifiers = (1..1000).map { WebhookTokenGenerator.generateEndpointIdentifier() }.toSet()
		assertEquals(1000, identifiers.size)
		identifiers.forEach { assertDoesNotThrow { UUID.fromString(it) } }
	}
}
