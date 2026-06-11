package com.glinboy.opportune.service

import java.util.UUID

interface ResumeTextFormatterService {
	fun formatResumeAsText(profileId: UUID): String
}
