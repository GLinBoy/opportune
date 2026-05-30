package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AdminScoreDistributionDTO
import com.glinboy.opportune.dto.AiQueueItemDTO
import com.glinboy.opportune.service.AdminAiService
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
@RequestMapping("/api/admin/ai")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminAiResource(private val adminAiService: AdminAiService) {

    @GetMapping("/queue")
    @PageableAsQueryParam
    fun getQueue(
        @Parameter(hidden = true) pageable: org.springframework.data.domain.Pageable,
        request: HttpServletRequest,
    ): ResponseEntity<List<AiQueueItemDTO>> {
        val page = adminAiService.getQueue(pageable)
        val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
        headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
        return ResponseEntity(page.content, headers, HttpStatus.OK)
    }

    @PostMapping("/retry/{applicationId}")
    fun retryJob(@PathVariable applicationId: UUID): ResponseEntity<Void> {
        adminAiService.retryJob(applicationId)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/score-distribution")
    fun getScoreDistribution(): ResponseEntity<AdminScoreDistributionDTO> =
        ResponseEntity.ok(adminAiService.getScoreDistribution())
}
