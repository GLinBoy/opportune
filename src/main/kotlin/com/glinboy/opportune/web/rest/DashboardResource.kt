package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.UserDashboardSummaryDTO
import com.glinboy.opportune.service.DashboardService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class DashboardResource (private val dashboardService: DashboardService) {

	@GetMapping("/summery")
	fun summery(): ResponseEntity<UserDashboardSummaryDTO> = ResponseEntity
		.ok(dashboardService.getCurrentUserSummery())
}
