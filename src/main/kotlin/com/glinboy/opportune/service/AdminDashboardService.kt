package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AdminDashboardSummaryDTO

interface AdminDashboardService {
	fun getSummary(): AdminDashboardSummaryDTO
}
