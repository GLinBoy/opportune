package com.glinboy.opportune.service

import com.glinboy.opportune.dto.WorkExperienceBulletDTO
import com.glinboy.opportune.dto.WorkExperienceDTO
import java.util.*

interface WorkExperienceService : GenericService<UUID, WorkExperienceDTO> {
	fun replaceBullets(workExperienceId: UUID, bullets: List<WorkExperienceBulletDTO>): List<WorkExperienceBulletDTO>
}
