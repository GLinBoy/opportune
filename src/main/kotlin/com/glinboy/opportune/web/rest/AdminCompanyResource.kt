package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AdminCompanyListItemDTO
import com.glinboy.opportune.dto.AdminCompanyStatusUpdateDTO
import com.glinboy.opportune.dto.AdminCompanyUpdateDTO
import com.glinboy.opportune.service.AdminCompanyService
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
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/companies")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminCompanyResource(private val adminCompanyService: AdminCompanyService) {

	@GetMapping
	@PageableAsQueryParam
	fun listCompanies(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "filter", required = false) filter: String?,
		request: HttpServletRequest,
	): ResponseEntity<List<AdminCompanyListItemDTO>> {
		val page = adminCompanyService.listCompanies(filter, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@PatchMapping("/{id}/status")
	fun updateStatus(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: AdminCompanyStatusUpdateDTO,
	): ResponseEntity<AdminCompanyListItemDTO> =
		ResponseEntity.ok(adminCompanyService.updateStatus(id, dto.status!!))

	@PatchMapping("/{id}")
	fun updateDetails(
		@PathVariable id: UUID,
		@RequestBody dto: AdminCompanyUpdateDTO,
	): ResponseEntity<AdminCompanyListItemDTO> =
		ResponseEntity.ok(adminCompanyService.updateDetails(id, dto))
}
