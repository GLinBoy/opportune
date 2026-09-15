package com.glinboy.opportune.security.webhook

import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.security.TokenHashUtils
import com.glinboy.opportune.service.WebhookTokenService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@ExtendWith(MockitoExtension::class)
class WebhookTokenAuthenticationFilterTest {

	@Mock
	private lateinit var webhookTokenService: WebhookTokenService

	private lateinit var filter: WebhookTokenAuthenticationFilter
	private lateinit var response: MockHttpServletResponse

	private val endpointId = "endpoint-123"
	private val rawToken = "opw_rawTokenValue"
	private val profileId: UUID = UUID.randomUUID()

	@BeforeEach
	fun setUp() {
		filter = WebhookTokenAuthenticationFilter(webhookTokenService)
		response = MockHttpServletResponse()
	}

	@AfterEach
	fun tearDown() {
		SecurityContextHolder.clearContext()
	}

	private fun request(token: String? = rawToken, endpoint: String = endpointId): MockHttpServletRequest {
		val request = MockHttpServletRequest("POST", "/api/webhook/$endpoint")
		request.requestURI = "/api/webhook/$endpoint"
		token?.let { request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $it") }
		return request
	}

	@Test
	fun `valid token and matching endpoint authenticate with the profile principal and ROLE_WEBHOOK`() {
		`when`(webhookTokenService.authenticate(TokenHashUtils.sha256Hex(rawToken), endpointId))
			.thenReturn(profileId)

		filter.doFilter(request(), response, MockFilterChain())

		val authentication = SecurityContextHolder.getContext().authentication
		assertNotNull(authentication)
		assertEquals(profileId.toString(), authentication!!.principal)
		assertTrue(authentication.authorities.any { it.authority == Role.ROLE_WEBHOOK.name })
		verify(webhookTokenService).authenticate(TokenHashUtils.sha256Hex(rawToken), endpointId)
	}

	@Test
	fun `endpoint mismatch leaves the request unauthenticated`() {
		`when`(webhookTokenService.authenticate(TokenHashUtils.sha256Hex(rawToken), endpointId))
			.thenReturn(null)

		filter.doFilter(request(), response, MockFilterChain())

		assertNull(SecurityContextHolder.getContext().authentication)
	}

	@Test
	fun `missing bearer token never hits the token service`() {
		filter.doFilter(request(token = null), response, MockFilterChain())

		assertNull(SecurityContextHolder.getContext().authentication)
		verifyNoInteractions(webhookTokenService)
	}

	@Test
	fun `non webhook path is skipped by the filter`() {
		val request = MockHttpServletRequest("GET", "/api/applications/list")
		request.requestURI = "/api/applications/list"
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")

		filter.doFilter(request, response, MockFilterChain())

		assertNull(SecurityContextHolder.getContext().authentication)
		verifyNoInteractions(webhookTokenService)
	}
}
