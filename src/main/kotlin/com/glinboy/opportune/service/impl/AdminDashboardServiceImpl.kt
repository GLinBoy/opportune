package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.*
import com.glinboy.opportune.service.AdminDashboardService
import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.service.SessionService
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AdminDashboardServiceImpl(
	private val profileService: ProfileService,
	private val companyService: CompanyService,
	private val sessionService: SessionService,
	private val applicationService: ApplicationService,
	private val properties: ApplicationProperties
) : AdminDashboardService {

	override fun getSummary(): AdminDashboardSummaryDTO {
		val trendFrom = Instant.now().minus(properties.config.dashboard.trendDays, ChronoUnit.DAYS)

		return AdminDashboardSummaryDTO(
			totalUsers = profileService.countAll(),
			totalCompanies = companyService.countAll(),
			activeSessions = sessionService.countActiveSessions(),
			aiQueueCount = applicationService.getAiQueueCount(),
			trendDays = properties.config.dashboard.trendDays,
			userRegistrationTrend = profileService.getRegistrationTrend(trendFrom),
			accountStatusDistribution = profileService.getAccountStatusDistribution(),
			applicationStatusDistribution = applicationService.getApplicationStatusDistribution(),
			aiQueueItems = applicationService.findAiQueueItems(properties.config.dashboard.aiQueuePreviewSize)
		)
	}
}
