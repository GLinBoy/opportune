package com.glinboy.opportune.util

import java.security.SecureRandom
import java.util.*

object WebhookTokenGenerator {

	const val TOKEN_PREFIX = "opw_"
	private const val TOKEN_RANDOM_BYTES = 32

	private val secureRandom = SecureRandom()
	private val encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()

	fun generateRawToken(): String {
		val randomBytes = ByteArray(TOKEN_RANDOM_BYTES)
		secureRandom.nextBytes(randomBytes)
		return TOKEN_PREFIX + encoder.encodeToString(randomBytes)
	}

	fun generateEndpointIdentifier(): String = UUID.randomUUID().toString()
}
