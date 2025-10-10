package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
		val page: Page<ApplicationProjection> = service.findAllApplications(pageable)
		val headers: HttpHeaders =
			PaginationUtil.generatePaginationHttpHeaders(page, request.requestURI)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{id}/details")
	fun getApplicationsDetailsById(@PathVariable id: UUID): ResponseEntity<ApplicationDetailsDTO> {
		return service.getApplicationDetails(id)
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())
	}
}
