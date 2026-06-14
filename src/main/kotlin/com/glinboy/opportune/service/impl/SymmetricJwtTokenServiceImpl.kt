package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.enums.SessionStatus
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.GeoLocationService
import com.glinboy.opportune.service.JwtTokenService
import com.glinboy.opportune.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Primary
class SymmetricJwtTokenServiceImpl(
	private val jwtEncoder: JwtEncoder,
	private val properties: ApplicationProperties,
	private val sessionService: SessionService,
	private val request: HttpServletRequest,
	private val userAgentAnalyzer: UserAgentAnalyzer,
	private val geoLocationService: GeoLocationService
): JwtTokenService {

	override fun generateTokens(profileDTO: ProfileDTO, rememberMe: Boolean): AccessTokenResponseDTO {
		val authorities: Set<String> = profileDTO.roles.map { it.name }.toSet()
		val now = Instant.now()
		val accessTokenValidityInSeconds = getTokenValidity(rememberMe)
		val refreshTokenValidityInSeconds = properties.security.authentication
			.jwt.refreshTokenValidityInSeconds ?: 2592000L

		val accessTokenClaims = accessTokenClaimsBuilder(now, accessTokenValidityInSeconds, profileDTO, authorities).build()
		val refreshTokenClaims = refreshTokenClaimsBuilder(now, refreshTokenValidityInSeconds, profileDTO).build()

		val accessToken = encodeClaims(accessTokenClaims)
		val refreshToken = encodeClaims(refreshTokenClaims)

		val clientIp = request.getHeader(properties.web.forwardedForHeader)
			?.split(",")?.firstOrNull()?.trim()
			?: request.remoteAddr
		val clientAgent = request.getHeader("User-Agent")
		val deviceId = request.getHeader("X-Device-Id")
		val clientGeo = geoLocationService.format(geoLocationService.lookup(clientIp))

		val (deviceType, os, browser, isMobile) = clientAgent
			?.let { userAgentAnalyzer.parse(it) }
			?.let { ua ->
				val deviceClass = ua.getValue("DeviceClass").takeUnless { it == "??" }
				val osName     = ua.getValue("OperatingSystemName").takeUnless { it == "??" }
				val agentName  = ua.getValue("AgentName").takeUnless { it == "??" }
				val mobile     = deviceClass in setOf("Phone", "Tablet")
				UserAgentInfo(deviceClass, osName, agentName, mobile)
			} ?: UserAgentInfo()

		sessionService.save(
			SessionDTO(
				refreshTokenId = refreshTokenClaims.id?.let(UUID::fromString),
				accessTokenId = accessTokenClaims.id?.let(UUID::fromString),
				accessTokenExpiration = now.plusSeconds(accessTokenValidityInSeconds),
				refreshTokenExpiration = now.plusSeconds(refreshTokenValidityInSeconds),
				status = SessionStatus.ACTIVE,
				lastActiveAt = now,
				clientAgent = clientAgent,
				clientIp = clientIp,
				clientGeo = clientGeo,
				deviceId = deviceId,
				deviceType = deviceType,
				os = os,
				browser = browser,
				isMobile = isMobile,
				loginAt = now,
				profileId = profileDTO.id
			)
		)


		return AccessTokenResponseDTO(
			accessToken = accessToken,
			expiresIn = accessTokenValidityInSeconds,
			refreshToken = refreshToken,
			refreshExpiresIn = refreshTokenValidityInSeconds,
			tokenType = SecurityUtils.TYPE_TOKEN_BEARER
		)
	}

	override fun refreshAccessToken(refreshJwt: Jwt, profileDTO: ProfileDTO): AccessTokenResponseDTO {
		val now = Instant.now()
		val refreshExpiresAt = refreshJwt.expiresAt
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is missing expiry")
		val remainingRefreshSeconds = ChronoUnit.SECONDS.between(now, refreshExpiresAt)
		if (remainingRefreshSeconds <= 0) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired")
		}
		val normalValidityInSeconds = properties.security.authentication.jwt.tokenValidityInSeconds ?: 2592000L
		val accessTokenValidityInSeconds = minOf(normalValidityInSeconds, remainingRefreshSeconds)
		val authorities: Set<String> = profileDTO.roles.map { it.name }.toSet()
		val accessTokenClaims = accessTokenClaimsBuilder(now, accessTokenValidityInSeconds, profileDTO, authorities).build()

		val refreshTokenId = refreshJwt.id?.let(UUID::fromString)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token carries no 'jti' claim")
		val session = sessionService.findByRefreshTokenId(refreshTokenId)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found for the provided refresh token")
		if (session.status != SessionStatus.ACTIVE) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session associated with the refresh token is not active")
		}
		sessionService.save(
			session.copy(
				accessTokenId = accessTokenClaims.id?.let(UUID::fromString),
				accessTokenExpiration = now.plusSeconds(accessTokenValidityInSeconds),
				lastRefreshedAt = now
			)
		)

		return AccessTokenResponseDTO(
			accessToken = encodeClaims(accessTokenClaims),
			expiresIn = accessTokenValidityInSeconds,
			refreshToken = refreshJwt.tokenValue,
			refreshExpiresIn = remainingRefreshSeconds,
			tokenType = SecurityUtils.TYPE_TOKEN_BEARER
		)
	}

	private fun getTokenValidity(rememberMe: Boolean): Long =
		properties.security.authentication.jwt.tokenValidityInSecondsForRememberMe
		.takeIf { rememberMe }
		?: properties.security.authentication.jwt.tokenValidityInSeconds
		?: 3600L

	private fun encodeClaims(claims: JwtClaimsSet): String =
		jwtEncoder.encode(JwtEncoderParameters.from(
			JwsHeader.with(MacAlgorithm.HS512)
			.type("JWT")
			.build(), claims))
			.tokenValue

	private fun commonsClaimsBuilder(
		now: Instant,
		profileDTO: ProfileDTO,
	): JwtClaimsSet.Builder = JwtClaimsSet.builder()
		.issuedAt(now)
		.notBefore(now)
		.id(UUID.randomUUID().toString())
		.issuer(properties.info.name.orEmpty())    // FIXME Use application URL as issuer
		.audience(listOf("${properties.info.name}-client"))
		.subject(profileDTO.id.toString())
		.claim(SecurityUtils.TYPE_CLAIM, SecurityUtils.TYPE_TOKEN_BEARER)
		.claim(SecurityUtils.CLIENT_ID_CLAIM, properties.info.name.orEmpty())
		.claim(SecurityUtils.SESSION_STATE_CLAIM, UUID.randomUUID().toString())

	private fun accessTokenClaimsBuilder(
		now: Instant,
		validityInSeconds: Long,
		profileDTO: ProfileDTO,
		authorities: Set<String>,
	): JwtClaimsSet.Builder = commonsClaimsBuilder(now, profileDTO)
		.audience(listOf(SecurityUtils.AUDIENCE_ACCOUNT))
		.expiresAt(now.plus(validityInSeconds, ChronoUnit.SECONDS))
		.claim(SecurityUtils.EMAIL_CLAIM, profileDTO.email.orEmpty())
		.claim(SecurityUtils.EMAIL_VERIFIED_CLAIM, profileDTO.emailVerification ?: false)
		.claim(SecurityUtils.FORENAME_CLAIM, profileDTO.forename.orEmpty())
		.claim(SecurityUtils.SURNAME_CLAIM, profileDTO.surname.orEmpty())
		.claim(SecurityUtils.NAME_CLAIM, "${profileDTO.forename.orEmpty()} ${profileDTO.surname.orEmpty()}".trim())
		.claim(SecurityUtils.AUTHORITIES_CLAIM, authorities)

	private fun refreshTokenClaimsBuilder(
		now: Instant,
		validityInSeconds: Long,
		profileDTO: ProfileDTO,
	): JwtClaimsSet.Builder = commonsClaimsBuilder(now, profileDTO)
		.audience(listOf(SecurityUtils.AUDIENCE_SECURITY))
		.expiresAt(now.plus(validityInSeconds, ChronoUnit.SECONDS))
		.claim(SecurityUtils.TYPE_CLAIM, SecurityUtils.TYPE_TOKEN_REFRESH)

	private data class UserAgentInfo(
		val deviceType: String? = null,
		val os: String? = null,
		val browser: String? = null,
		val isMobile: Boolean = false,
	)
}
