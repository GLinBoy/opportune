package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.dto.ApplicationUrlSubmissionDTO
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
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
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/applications")
open class ApplicationResource(applicationService: ApplicationService) :
	GenericResource<UUID, ApplicationDTO, ApplicationService>(applicationService) {

	@GetMapping("/list")
	@PageableAsQueryParam
	fun getApplications(
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<ApplicationProjection>> {
		val page: Page<ApplicationProjection> = service.findAllApplicationsForCurrentUser(pageable)
		val headers: HttpHeaders =
			PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{id}/details")
	fun getApplicationsDetailsById(@PathVariable id: UUID): ResponseEntity<ApplicationDetailsDTO> {
		return service.getApplicationDetailsForCurrentUser(id)
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())
	}

	@PostMapping("/submit-url")
	fun submitApplicationUrl(
		@Valid @RequestBody submission: ApplicationUrlSubmissionDTO
	): ResponseEntity<ApplicationDTO> {
		log.info("Received URL submission: ${submission.url}")

		// TODO: Implement URL fetching and application creation logic

		return service.findById(UUID.fromString("770e8400-e29b-41d4-a716-446655440056"))
			.map {
				ResponseEntity.created(URI.create("/api/applications/${it.id}"))
					.contentType(MediaType.APPLICATION_JSON)
					.body(it)
			}
			.orElse(ResponseEntity.internalServerError().build())
	}
}
