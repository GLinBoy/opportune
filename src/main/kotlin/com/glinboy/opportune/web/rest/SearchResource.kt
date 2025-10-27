package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.SearchResultDTO
import com.glinboy.opportune.service.SearchService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class SearchResource(private val searchService: SearchService) {

	@PageableAsQueryParam
	@GetMapping
	fun search(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "query")
		@NotBlank(message = "Query parameter cannot be blank")
		@Size(min = 1, max = 255, message = "Query must be between 1 and 255 characters")
		query: String,
		request: HttpServletRequest
	): ResponseEntity<List<SearchResultDTO>> {
		val sanitizedQuery = sanitizeQuery(query)
		val page = searchService.searchForCurrentUser(sanitizedQuery, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@PageableAsQueryParam
	@GetMapping("/companies")
	fun searchCompanies(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "query")
		@NotBlank(message = "Query parameter cannot be blank")
		@Size(min = 1, max = 255, message = "Query must be between 1 and 255 characters")
		query: String,
		request: HttpServletRequest
	): ResponseEntity<List<SearchResultDTO>> {
		val sanitizedQuery = sanitizeQuery(query)
		val page = searchService.searchCompaniesForCurrentUser(sanitizedQuery, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@PageableAsQueryParam
	@GetMapping("/applications")
	fun searchApplications(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "query")
		@NotBlank(message = "Query parameter cannot be blank")
		@Size(min = 1, max = 255, message = "Query must be between 1 and 255 characters")
		query: String,
		request: HttpServletRequest
	): ResponseEntity<List<SearchResultDTO>> {
		val sanitizedQuery = sanitizeQuery(query)
		val page = searchService.searchApplicationsForCurrentUser(sanitizedQuery, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	/**
	 * Sanitizes the search query by trimming whitespace and removing potentially harmful characters.
	 * This is defense in depth - the parameterized queries already prevent SQL injection.
	 */
	private fun sanitizeQuery(query: String): String {
		return query.trim()
			.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "") // Remove control characters
			.take(255) // Enforce max length as additional safety
	}
}
