package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.service.ApplicationMetaDataService
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
class ApplicationMetaDataResource(private val applicationMetaDataService: ApplicationMetaDataService) {

	@PageableAsQueryParam
	@GetMapping("/{application_id}/metadata")
	fun getApplicationMetaData(
		@PathVariable("application_id") applicationId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<ApplicationMetaDataDTO>> {
		val page: Page<ApplicationMetaDataDTO> = applicationMetaDataService.findAllForCurrentUser(applicationId, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{application_id}/metadata/{id}")
	fun getApplicationMetaData(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<ApplicationMetaDataDTO> = applicationMetaDataService.findByIdForCurrentUser(applicationId, id)
		.map { ResponseEntity.ok().body(it) }
		.orElse(ResponseEntity.notFound().build())

	@PostMapping("/{application_id}/metadata")
	fun saveApplicationMetaData(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody applicationMetaDataDTO: ApplicationMetaDataDTO,
		request: HttpServletRequest
	): ResponseEntity<ApplicationMetaDataDTO> {
		if (applicationId != (applicationMetaDataDTO.applicationId)) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		val savedApplicationMetaDataDTO = applicationMetaDataService.save(applicationMetaDataDTO)
		val location = URI.create("${request.requestURI}/${savedApplicationMetaDataDTO.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(savedApplicationMetaDataDTO)
	}

	@PutMapping("/{application_id}/metadata")
	fun updateApplicationMetaData(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody applicationMetaDataDTO: ApplicationMetaDataDTO
	): ResponseEntity<ApplicationMetaDataDTO> {
		if (applicationMetaDataDTO.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		if (applicationId != applicationMetaDataDTO.applicationId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		return ResponseEntity.ok().body(applicationMetaDataService.update(applicationMetaDataDTO))
	}

	@DeleteMapping("/{application_id}/metadata/{id}")
	fun deleteApplicationMetaData(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Unit> {
		applicationMetaDataService.delete(applicationId, id)
		return ResponseEntity.noContent().build()
	}
}
