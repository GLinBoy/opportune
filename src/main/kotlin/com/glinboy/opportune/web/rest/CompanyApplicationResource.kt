package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationDTO
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
@RequestMapping("/api/companies")
class CompanyApplicationResource(private val applicationService: ApplicationService) {

	@PageableAsQueryParam
	@GetMapping("/{company_id}/applications")
	fun getCompanyApplication(
		@PathVariable("company_id") companyId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<ApplicationDTO>> {
		val page: Page<ApplicationDTO> = applicationService.getCompanyApplications(companyId, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request.requestURI)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}
}
