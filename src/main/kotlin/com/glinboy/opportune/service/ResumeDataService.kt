package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ResumeAggregateDTO

interface ResumeDataService {
	fun getAggregateForCurrentUser(): ResumeAggregateDTO
}
