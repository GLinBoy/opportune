package com.glinboy.opportune.security.webhook

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

/**
 * Entry point for the dedicated webhook security chain. Unlike the session/JWT chain it always
 * answers with a JSON 401 body and never advertises a bearer-token challenge.
 */
@Component
class WebhookAuthenticationEntryPoint : AuthenticationEntryPoint {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun commence(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authException: AuthenticationException
	) {
		log.debug("Rejecting unauthenticated webhook request for {}", request.requestURI)
		response.status = HttpServletResponse.SC_UNAUTHORIZED
		response.contentType = MediaType.APPLICATION_JSON_VALUE
		response.characterEncoding = StandardCharsets.UTF_8.name()
		response.writer.write(
			"""{"status":401,"error":"Unauthorized","message":"Invalid or missing webhook credentials"}"""
		)
	}
}
