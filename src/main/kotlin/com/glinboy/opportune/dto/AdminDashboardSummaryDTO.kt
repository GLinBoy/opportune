package com.glinboy.opportune.dto

data class AdminDashboardSummaryDTO(
	val totalUsers: Long,
	val totalCompanies: Long,
	val activeSessions: Long,
	val aiQueueCount: Long,
	val trendDays: Long,
	val userRegistrationTrend: List<UserRegistrationTrendPointDTO>,
	val accountStatusDistribution: List<AccountStatusCountDTO>,
	val applicationStatusDistribution: List<ApplicationStatusCountDTO>,
	val aiQueueItems: List<AiQueueItemDTO>
)
