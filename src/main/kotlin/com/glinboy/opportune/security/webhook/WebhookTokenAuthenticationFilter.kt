package com.glinboy.opportune.security.webhook

import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.security.TokenHashUtils
import com.glinboy.opportune.service.WebhookTokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Authenticates requests against the dedicated webhook chain. The raw bearer token is hashed and
 * looked up together with the `{endpointId}` path segment; both must resolve to the same ACTIVE row.
 * On success the owning profile UUID becomes the principal with `ROLE_WEBHOOK`. The token value is
 * never logged.
 */
class WebhookTokenAuthenticationFilter(
	private val webhookTokenService: WebhookTokenService
) : OncePerRequestFilter() {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		val endpointIdentifier = extractEndpointIdentifier(request)
		val rawToken = extractBearerToken(request)

		if (endpointIdentifier == null || rawToken == null) {
			log.debug("Webhook request to {} carries no usable endpoint identifier or bearer token", request.requestURI)
			filterChain.doFilter(request, response)
			return
		}

		val profileId = webhookTokenService.authenticate(TokenHashUtils.sha256Hex(rawToken), endpointIdentifier)
		if (profileId == null) {
			log.debug("Webhook request for endpoint {} rejected: token missing, revoked or mismatched", endpointIdentifier)
			filterChain.doFilter(request, response)
			return
		}

		val authentication = UsernamePasswordAuthenticationToken(
			profileId.toString(),
			null,
			listOf(SimpleGrantedAuthority(Role.ROLE_WEBHOOK.name))
		)
		SecurityContextHolder.getContext().authentication = authentication
		filterChain.doFilter(request, response)
	}

	override fun shouldNotFilter(request: HttpServletRequest): Boolean =
		resolvePath(request)?.startsWith(WEBHOOK_PATH_PREFIX) != true

	private fun extractBearerToken(request: HttpServletRequest): String? {
		val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
		if (!header.regionMatches(0, BEARER_PREFIX, 0, BEARER_PREFIX.length, ignoreCase = true)) return null
		return header.substring(BEARER_PREFIX.length).trim().ifBlank { null }
	}

	private fun extractEndpointIdentifier(request: HttpServletRequest): String? {
		val path = resolvePath(request) ?: return null
		if (!path.startsWith(WEBHOOK_PATH_PREFIX)) return null
		val remainder = path.substring(WEBHOOK_PATH_PREFIX.length)
		return remainder.takeIf { it.isNotEmpty() && !it.contains('/') }
	}

	private fun resolvePath(request: HttpServletRequest): String? =
		request.requestURI?.removePrefix(request.contextPath ?: "")

	companion object {
		private const val BEARER_PREFIX = "Bearer "
		private const val WEBHOOK_PATH_PREFIX = "/api/webhook/"
	}
}
