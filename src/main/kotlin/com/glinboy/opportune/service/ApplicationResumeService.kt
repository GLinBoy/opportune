package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationResumeDTO
import java.util.*

interface ApplicationResumeService : GenericService<UUID, ApplicationResumeDTO> {
	fun findByApplicationId(applicationId: UUID): Optional<ApplicationResumeDTO>
	fun deleteByApplicationId(applicationId: UUID): Unit
}
