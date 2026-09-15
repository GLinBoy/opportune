package com.glinboy.opportune.security

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

object TokenHashUtils {

	private const val HASH_ALGORITHM = "SHA-256"

	fun sha256Hex(token: String): String =
		HexFormat.of().formatHex(
			MessageDigest.getInstance(HASH_ALGORITHM)
				.digest(token.toByteArray(StandardCharsets.UTF_8))
		)
}
