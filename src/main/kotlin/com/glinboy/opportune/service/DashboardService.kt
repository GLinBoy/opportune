package com.glinboy.opportune.service

import com.glinboy.opportune.dto.UserDashboardSummaryDTO
import com.glinboy.opportune.projection.ApplicationStatProjection

interface DashboardService {
	fun getCurrentUserSummery(): UserDashboardSummaryDTO
}
