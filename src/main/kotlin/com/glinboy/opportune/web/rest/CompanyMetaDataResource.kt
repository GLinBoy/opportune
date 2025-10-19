package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.service.CompanyMetaDataService
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
@RequestMapping("/api/companies")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class CompanyMetaDataResource(private val companyMetaDataService: CompanyMetaDataService) {

	@PageableAsQueryParam
	@GetMapping("/{company_id}/metadata")
	fun getCompanyMetaData(
		@PathVariable("company_id") companyId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<CompanyMetaDataDTO>> {
		val page: Page<CompanyMetaDataDTO> = companyMetaDataService.findAll(companyId, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{company_id}/metadata/{id}")
	fun getCompanyMetaData(
		@PathVariable("company_id") companyId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<CompanyMetaDataDTO> = companyMetaDataService.findById(companyId, id)
		.map { ResponseEntity.ok().body(it) }
		.orElse(ResponseEntity.notFound().build())

	@PostMapping("/{company_id}/metadata")
	fun saveCompanyMetaData(
		@PathVariable("company_id") companyId: UUID,
		@Valid @RequestBody companyMetaDataDTO: CompanyMetaDataDTO,
		request: HttpServletRequest
	): ResponseEntity<CompanyMetaDataDTO> {
		if (companyId != (companyMetaDataDTO.companyId)) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Company ID in path must match company ID in body"
			)
		}
		val savedCompanyMetaDataDTO = companyMetaDataService.save(companyMetaDataDTO)
		val location = URI.create("${request.requestURI}/${savedCompanyMetaDataDTO.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(savedCompanyMetaDataDTO)
	}

	@PutMapping("/{company_id}/metadata")
	fun updateCompanyMetaData(
		@PathVariable("company_id") companyId: UUID,
		@Valid @RequestBody companyMetaDataDTO: CompanyMetaDataDTO
	): ResponseEntity<CompanyMetaDataDTO> {
		if (companyMetaDataDTO.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		if (companyId != companyMetaDataDTO.companyId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Company ID in path must match company ID in body"
			)
		}
		return ResponseEntity.ok().body(companyMetaDataService.update(companyMetaDataDTO))
	}

	@DeleteMapping("/{company_id}/metadata/{id}")
	fun deleteCompanyMetaData(
		@PathVariable("company_id") companyId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Unit> {
		companyMetaDataService.delete(companyId, id)
		return ResponseEntity.noContent().build()
	}
}
