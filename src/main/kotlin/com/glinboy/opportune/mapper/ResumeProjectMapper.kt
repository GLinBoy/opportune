package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ResumeProjectDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ResumeProject
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ResumeProjectMapper : GenericMapper<ResumeProjectDTO, ResumeProject> {
	override fun createEntity(dto: ResumeProjectDTO): ResumeProject {
		return ResumeProject(
			id = null,
			name = dto.name,
			description = dto.description,
			url = dto.url,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
			techStack = dto.techStack.toMutableList()
		)
	}

	override fun updateEntity(dto: ResumeProjectDTO, entity: ResumeProject): ResumeProject {
		return entity.copy(
			id = entity.id,
			name = dto.name,
			description = dto.description,
			url = dto.url,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile,
			techStack = dto.techStack.toMutableList()
		)
	}

	override fun toDto(entity: ResumeProject): ResumeProjectDTO {
		return ResumeProjectDTO(
			id = entity.id,
			name = entity.name,
			description = entity.description,
			url = entity.url,
			startMonth = entity.startMonth,
			startYear = entity.startYear,
			endMonth = entity.endMonth,
			endYear = entity.endYear,
			isCurrent = entity.isCurrent,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id,
			techStack = entity.techStack
		)
	}
}
