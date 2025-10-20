package com.glinboy.opportune.security.jwt

import com.glinboy.opportune.security.SecurityUtils
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

	private val defaultGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

	override fun convert(jwt: Jwt): AbstractAuthenticationToken {
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

