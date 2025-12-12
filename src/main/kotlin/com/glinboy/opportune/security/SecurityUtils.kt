package com.glinboy.opportune.security

import com.glinboy.opportune.enums.Role
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

object SecurityUtils {
	val JWT_ALGORITHM = MacAlgorithm.HS512
	const val TOKEN_TYPE_BEARER = "Bearer"
	const val TYPE_CLAIM = "typ"
	const val CLIENT_ID_CLAIM = "azp"
	const val SESSION_STATE_CLAIM = "session_state"
	const val NAME_CLAIM = "name"
	const val FORENAME_CLAIM = "forename"
	const val SURNAME_CLAIM = "surname"
	const val EMAIL_CLAIM = "email"
	const val AUTHORITIES_CLAIM = "roles"
	const val EMAIL_VERIFIED_CLAIM = "email_verified"

	fun getCurrentUserLogin(): String? =
		extractPrincipal(SecurityContextHolder.getContext().authentication)

	fun getCurrentUserLoginID(): UUID = getCurrentUserLogin().let(UUID::fromString)

	fun extractPrincipal(authentication: Authentication?): String? {
		if (authentication == null) {
			return null
		}
		return when (val principal = authentication.principal) {
			is UserDetails -> principal.username
			is Jwt -> principal.subject
			is String -> principal
			else -> null
		}
	}

	fun getCurrentUserJWT(): Optional<String> =
		Optional.ofNullable(SecurityContextHolder.getContext().authentication)
			.filter { it.credentials is String }
			.map { it.credentials as String }

	fun isAuthenticated(): Boolean {
		val authentication = SecurityContextHolder.getContext().authentication
		if (authentication != null) {
			return getAuthorities(authentication).none { it == Role.ROLE_ANONYMOUS.name }
		}
		return false
	}

	fun isCurrentUserInRole(authority: String): Boolean {
		val authentication = SecurityContextHolder.getContext().authentication
		if (authentication != null) {
			return getAuthorities(authentication).any { it == authority }
		}
		return false
	}

	fun getAuthorities(authentication: Authentication): List<String> {
		return authentication.authorities.mapNotNull(GrantedAuthority::getAuthority)
	}
}
