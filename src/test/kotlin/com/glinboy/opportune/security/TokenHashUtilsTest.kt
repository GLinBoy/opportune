package com.glinboy.opportune.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TokenHashUtilsTest {

	@Test
	fun `sha256Hex matches the known SHA-256 vector for abc`() {
		assertEquals(
			"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
			TokenHashUtils.sha256Hex("abc")
		)
	}

	@Test
	fun `sha256Hex returns 64 lowercase hex characters`() {
		val hash = TokenHashUtils.sha256Hex("opw_example-token")
		assertEquals(64, hash.length)
		assertTrue(hash.matches(Regex("^[0-9a-f]{64}$")))
	}

	@Test
	fun `sha256Hex is deterministic for the same input`() {
		assertEquals(TokenHashUtils.sha256Hex("token"), TokenHashUtils.sha256Hex("token"))
	}

	@Test
	fun `sha256Hex differs for different inputs`() {
		assertNotEquals(TokenHashUtils.sha256Hex("token-a"), TokenHashUtils.sha256Hex("token-b"))
	}
}
