package com.glinboy.opportune.dto

import com.glinboy.opportune.projection.ApplicationStatProjection
import com.glinboy.opportune.projection.ScoreSummaryProjection

data class UserDashboardSummaryDTO(
	val stats: List<ApplicationStatProjection>,
	val scores: ScoreSummaryProjection
)
