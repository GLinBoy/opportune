package com.glinboy.opportune.security.jwt

import com.glinboy.opportune.enums.SessionStatus
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.SessionService
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class JwtAuthenticationConverter(
	private val sessionService: SessionService
) : Converter<Jwt, AbstractAuthenticationToken> {

	private val defaultGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

	override fun convert(jwt: Jwt): AbstractAuthenticationToken {
		val jti = jwt.id
			?: throw OAuth2AuthenticationException(OAuth2Error("invalid_token", "Access token carries no 'jti' claim", null))

		val session = sessionService.findByAccessTokenId(UUID.fromString(jti))
			?: throw OAuth2AuthenticationException(OAuth2Error("invalid_token", "Session not found for the provided access token", null))

		if (session.status != SessionStatus.ACTIVE) {
			throw OAuth2AuthenticationException(OAuth2Error("invalid_token", "Session associated with this access token is no longer active", null))
		}

		sessionService.update(session.copy(lastActiveAt = Instant.now()))

		val authorities = extractAuthorities(jwt)
		return JwtAuthenticationToken(jwt, authorities)
	}

	private fun extractAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
		// Extract roles from custom "roles" claim
		val roles = jwt.getClaimAsStringList(SecurityUtils.AUTHORITIES_CLAIM) ?: emptyList()
		val customAuthorities = roles.map { SimpleGrantedAuthority(it) }

		// Also include any default authorities (from scope/scp claims)
		val defaultAuthorities = defaultGrantedAuthoritiesConverter.convert(jwt) ?: emptyList()

		return customAuthorities + defaultAuthorities
	}
}

