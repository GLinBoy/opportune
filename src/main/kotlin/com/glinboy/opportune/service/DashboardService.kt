package com.glinboy.opportune.service

import com.glinboy.opportune.dto.UserDashboardSummaryDTO

interface DashboardService {
	fun getCurrentUserSummery(): UserDashboardSummaryDTO
}
