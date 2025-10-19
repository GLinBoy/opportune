package com.glinboy.opportune.security.jwt

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.entity.Profile
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class JwtService(
	private val jwtEncoder: JwtEncoder,
	private val properties: ApplicationProperties
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	fun createToken(profile: Profile, rememberMe: Boolean = false): AccessTokenResponseDTO {
		val authorities: Set<String> = profile.roles.map { it.name }.toSet()
		val now = Instant.now()
		val validityInSeconds =
			if (rememberMe)
				properties.security.authentication.jwt.tokenValidityInSecondsForRememberMe!!
			else
				properties.security.authentication.jwt.tokenValidityInSeconds!!
		val builder: JwtClaimsSet.Builder = JwtClaimsSet.builder()
			.expiresAt(now.plus(validityInSeconds, ChronoUnit.SECONDS))
			.issuedAt(now)
			.id(UUID.randomUUID().toString())
			.issuer("opportune")
			.audience(listOf("opportune-client"))
			.subject(profile.id.toString())
			.claim("typ", "Bearer")
			.claim("azp", "opportune")
			.claim("session_state", UUID.randomUUID().toString())
			.claim("name", "${profile.forename.orEmpty()} ${profile.surname.orEmpty()}".trim())
			.claim("forename", profile.forename)
			.claim("surname", profile.surname)
			.claim("email", profile.email)
			.claim("roles", authorities)
			.claim("email_verified", profile.emailVerification ?: false)
		val jwsHeader: JwsHeader = JwsHeader.with(MacAlgorithm.HS512).build()
		return AccessTokenResponseDTO(
			accessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue(),
			expiresIn = validityInSeconds,
			refreshToken = "refresh_token_example",
			refreshExpiresIn = 2_592_000L,
			tokenType = "Bearer"
		)
	}
}
