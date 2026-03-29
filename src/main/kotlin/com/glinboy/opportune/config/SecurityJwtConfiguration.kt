package com.glinboy.opportune.config

import com.glinboy.opportune.security.SecurityUtils
import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.util.Base64
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class SecurityJwtConfiguration(private val properties: ApplicationProperties) {

	private val log = LoggerFactory.getLogger(this::class.java)

	@Bean
	fun jwtDecoder(): JwtDecoder {
		val jwtDecoder = NimbusJwtDecoder
			.withSecretKey(getSecretKey())
			.macAlgorithm(SecurityUtils.JWT_ALGORITHM)
			.build()
		return JwtDecoder { token ->
			try {
				jwtDecoder.decode(token)
			} catch (e: Exception) {
				when {
					e.message?.contains("Invalid signature") == true -> {
						log.error("Invalid JWT signature: {}", e.message)
					}

					e.message?.contains("Jwt expired at") == true -> {
						log.error("Expired JWT token: {}", e.message)
					}

					e.message?.contains("Invalid JWT serialization") == true ||
						e.message?.contains("Malformed token") == true ||
						e.message?.contains("Invalid unsecured/JWS/JWE") == true -> {
						log.error("Invalid JWT token: {}", e.message)
					}

					else -> {
						log.error("Unknown JWT error {}", e.message)
					}
				}
				throw e
			}
		}
	}

	@Bean
	fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(ImmutableSecret(getSecretKey()))

	@Bean
	fun userAgentAnalyzer(): UserAgentAnalyzer = UserAgentAnalyzer.newBuilder()
		.withCache(10_000)
		.withFields("DeviceClass", "OperatingSystemName", "AgentName")
		.build()

	private fun getSecretKey(): SecretKey {
		val keyBytes: ByteArray = Base64.from(properties.security.authentication.jwt.base64Secret).decode()
		return SecretKeySpec(keyBytes, 0, keyBytes.size, SecurityUtils.JWT_ALGORITHM.name)
	}
}
