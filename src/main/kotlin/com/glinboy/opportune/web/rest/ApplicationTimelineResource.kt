package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.ApplicationTimelineDTO
import com.glinboy.opportune.service.ApplicationTimelineService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/applications")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ApplicationTimelineResource(private val applicationTimelineService: ApplicationTimelineService) {

	@PageableAsQueryParam
	@GetMapping("/{application_id}/timelines")
	fun getApplicationTimelines(
		@PathVariable("application_id") applicationId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<ApplicationTimelineDTO>> {
		val page: Page<ApplicationTimelineDTO> = applicationTimelineService.findAllForCurrentUser(applicationId, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{application_id}/timelines/{id}")
	fun getApplicationTimeline(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<ApplicationTimelineDTO> = applicationTimelineService.findByIdForCurrentUser(applicationId, id)
		.map { ResponseEntity.ok().body(it) }
		.orElse(ResponseEntity.notFound().build())

	@PostMapping("/{application_id}/timelines")
	fun saveApplicationTimeline(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody applicationTimelineDTO: ApplicationTimelineDTO,
		request: HttpServletRequest
	): ResponseEntity<ApplicationTimelineDTO> {
		if (applicationId != applicationTimelineDTO.applicationId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		val saved = applicationTimelineService.save(applicationTimelineDTO)
		val location = URI.create("${request.requestURI}/${saved.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/{application_id}/timelines")
	fun updateApplicationTimeline(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody applicationTimelineDTO: ApplicationTimelineDTO
	): ResponseEntity<ApplicationTimelineDTO> {
		if (applicationTimelineDTO.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		if (applicationId != applicationTimelineDTO.applicationId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		return ResponseEntity.ok().body(applicationTimelineService.updateForCurrentUser(applicationTimelineDTO))
	}

	@DeleteMapping("/{application_id}/timelines/{id}")
	fun deleteApplicationTimeline(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Unit> {
		applicationTimelineService.deleteForCurrentUser(applicationId, id)
		return ResponseEntity.noContent().build()
	}
}
