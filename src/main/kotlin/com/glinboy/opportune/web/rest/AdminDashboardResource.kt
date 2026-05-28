package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AdminDashboardSummaryDTO
import com.glinboy.opportune.service.AdminDashboardService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/dashboard")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminDashboardResource(private val adminDashboardService: AdminDashboardService) {

	@GetMapping("/summary")
	fun getSummary(): ResponseEntity<AdminDashboardSummaryDTO> =
		ResponseEntity.ok(adminDashboardService.getSummary())
}
