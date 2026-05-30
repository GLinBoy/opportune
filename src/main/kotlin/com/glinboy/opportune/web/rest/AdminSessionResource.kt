package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AdminSessionListItemDTO
import com.glinboy.opportune.service.AdminSessionService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/sessions")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminSessionResource(private val adminSessionService: AdminSessionService) {

	@GetMapping
	@PageableAsQueryParam
	fun listSessions(
		@Parameter(hidden = true) pageable: org.springframework.data.domain.Pageable,
		@RequestParam(value = "filter", required = false) filter: String?,
		request: HttpServletRequest,
	): ResponseEntity<List<AdminSessionListItemDTO>> {
		val page = adminSessionService.listSessions(filter, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@DeleteMapping("/{id}")
	fun revokeSession(@PathVariable id: UUID): ResponseEntity<Void> {
		adminSessionService.revokeSession(id)
		return ResponseEntity.noContent().build()
	}

	@PostMapping("/bulk-revoke/user/{profileId}")
	fun bulkRevokeByUser(@PathVariable profileId: UUID): ResponseEntity<Void> {
		adminSessionService.bulkRevokeByUser(profileId)
		return ResponseEntity.noContent().build()
	}

	@PostMapping("/bulk-revoke/ip")
	fun bulkRevokeByIp(@RequestParam ip: String): ResponseEntity<Void> {
		adminSessionService.bulkRevokeByIp(ip)
		return ResponseEntity.noContent().build()
	}
}
