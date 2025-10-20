package com.glinboy.opportune.security.jwt

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.security.SecurityUtils
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
			.issuer(properties.info.name)
			.audience(listOf("${properties.info.name}-client"))
			.subject(profile.id.toString())
			.claim(SecurityUtils.TYPE_CLAIM, SecurityUtils.TOKEN_TYPE_BEARER)
			.claim(SecurityUtils.CLIENT_ID_CLAIM, properties.info.name)
			.claim(SecurityUtils.SESSION_STATE_CLAIM, UUID.randomUUID().toString())
			.claim(SecurityUtils.NAME_CLAIM, "${profile.forename.orEmpty()} ${profile.surname.orEmpty()}".trim())
			.claim(SecurityUtils.FORENAME_CLAIM, profile.forename)
			.claim(SecurityUtils.SURNAME_CLAIM, profile.surname)
			.claim(SecurityUtils.EMAIL_CLAIM, profile.email)
			.claim(SecurityUtils.AUTHORITIES_CLAIM, authorities)
			.claim(SecurityUtils.EMAIL_VERIFIED_CLAIM, profile.emailVerification ?: false)
		val jwsHeader: JwsHeader = JwsHeader.with(MacAlgorithm.HS512).build()
		return AccessTokenResponseDTO(
			accessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).tokenValue,
			expiresIn = validityInSeconds,
			refreshToken = "refresh_token_example",
			refreshExpiresIn = 2_592_000L,
			tokenType = SecurityUtils.TOKEN_TYPE_BEARER
		)
	}
}
