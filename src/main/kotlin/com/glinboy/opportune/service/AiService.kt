package com.glinboy.opportune.service

import java.util.*

interface AiService {
	fun analysisApplication(applicationId: UUID)
	fun retryAnalysis(applicationId: UUID)
}
