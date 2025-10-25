package com.glinboy.opportune.util

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID

object UUIDBase64 {

	private val encoder = Base64.getUrlEncoder().withoutPadding()
	private val decoder = Base64.getUrlDecoder()
	private val uuidRegex = Regex("^[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}$")

	fun toBase64(uuid: UUID): String {
		val bb = ByteBuffer.allocate(16)
		bb.putLong(uuid.mostSignificantBits)
		bb.putLong(uuid.leastSignificantBits)
		return encoder.encodeToString(bb.array())
	}

	fun fromBase64(s: String): UUID {
		// If input already looks like a standard UUID string, parse directly
		if (uuidRegex.matches(s)) {
			return UUID.fromString(s)
		}

		val padded = addPaddingIfNeeded(s)
		val bytes = try {
			decoder.decode(padded)
		} catch (ex: IllegalArgumentException) {
			throw IllegalArgumentException("Input is not valid URL-safe Base64 or UUID string: $s", ex)
		}

		return when (bytes.size) {
			16 -> {
				// Binary UUID representation
				val bb = ByteBuffer.wrap(bytes)
				UUID(bb.long, bb.long)
			}
			36 -> {
				// Could be Base64-encoded textual UUID (ASCII). Try to parse it.
				val asString = String(bytes, StandardCharsets.UTF_8)
				if (uuidRegex.matches(asString)) {
					UUID.fromString(asString)
				} else {
					throw IllegalArgumentException("Decoded byte array length is 36 but does not contain a valid UUID string: $asString")
				}
			}
			else -> throw IllegalArgumentException("Invalid UUID byte array length: ${bytes.size}. Expected 16 (binary) or 36 (text)")
		}
	}

	private fun addPaddingIfNeeded(s: String): String {
		val mod = s.length % 4
		return if (mod == 0) s else s + "=".repeat(4 - mod)
	}
}

fun UUID.toBase64Url(): String = UUIDBase64.toBase64(this)
fun String.toUuidFromBase64(): UUID = UUIDBase64.fromBase64(this)
