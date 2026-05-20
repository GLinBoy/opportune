package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.UserDashboardSummaryDTO
import com.glinboy.opportune.projection.ApplicationStatProjection
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.service.DashboardService
import org.springframework.stereotype.Service

@Service
class DashboardServiceImpl(private val applicationService: ApplicationService): DashboardService {
	override fun getCurrentUserSummery(): UserDashboardSummaryDTO = applicationService
		.getUserSummery(SecurityUtils.getCurrentUserLoginID())

}
