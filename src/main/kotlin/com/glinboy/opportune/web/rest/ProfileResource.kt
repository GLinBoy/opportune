package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.PasswordUpdateRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.service.SessionService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ProfileResource(profileService: ProfileService, private val sessionService: SessionService) :
	GenericResource<UUID, ProfileDTO, ProfileService>(profileService) {

	@GetMapping("/me")
	fun getCurrentProfile(): ResponseEntity<ProfileDTO>? =
		service.getCurrentProfile()
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())

	@PageableAsQueryParam
	@GetMapping("/sessions")
	fun getSessions(@Parameter(hidden = true) pageable: Pageable,
									request: HttpServletRequest): ResponseEntity<List<SessionDTO>> {
		val page = sessionService.getSessionsForCurrentUser(pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@DeleteMapping("/sessions/{refreshTokenId}")
	fun terminateSession(@PathVariable refreshTokenId: UUID): ResponseEntity<Void> {
		log.debug("REST request to terminate session with refresh token ID: {}", refreshTokenId)
		sessionService.terminateSession(refreshTokenId)
		return ResponseEntity.noContent().build()
	}

	@PutMapping("/password/change")
	fun changePassword(
		@Valid @RequestBody passwordUpdateRequestDTO: PasswordUpdateRequestDTO
	): ResponseEntity<Void> {
		service.changePassword(passwordUpdateRequestDTO)
		return ResponseEntity.ok().build()
	}

	@PostMapping("/email/verify/request")
	fun verifyEmail(): ResponseEntity<Void> {
		// TODO: Implement resend activation email
		return ResponseEntity.ok().build()
	}

	@PutMapping("/email/confirm")
	fun confirmEmail(@RequestParam code: String): ResponseEntity<Void> {
			service.confirmEmail(code)
			return ResponseEntity.ok().build()
	}
}

