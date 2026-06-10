package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.WorkExperienceBulletDTO
import com.glinboy.opportune.dto.WorkExperienceDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.WorkExperience
import com.glinboy.opportune.entity.WorkExperienceBullet
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class WorkExperienceMapper : GenericMapper<WorkExperienceDTO, WorkExperience> {
	override fun createEntity(dto: WorkExperienceDTO): WorkExperience {
		return WorkExperience(
			id = null,
			jobTitle = dto.jobTitle,
			company = dto.company,
			location = dto.location,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
			bullets = emptySet()
		)
	}

	override fun updateEntity(dto: WorkExperienceDTO, entity: WorkExperience): WorkExperience {
		return entity.copy(
			id = entity.id,
			jobTitle = dto.jobTitle,
			company = dto.company,
			location = dto.location,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile ?: dto.profileId?.let { Profile(id = it) },
			bullets = entity.bullets
		)
	}

	override fun toDto(entity: WorkExperience): WorkExperienceDTO {
		return WorkExperienceDTO(
			id = entity.id,
			jobTitle = entity.jobTitle,
			company = entity.company,
			location = entity.location,
			startMonth = entity.startMonth,
			startYear = entity.startYear,
			endMonth = entity.endMonth,
			endYear = entity.endYear,
			isCurrent = entity.isCurrent,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id,
			bullets = entity.bullets.map { bullet ->
				WorkExperienceBulletDTO(
					id = bullet.id,
					content = bullet.content,
					displayOrder = bullet.displayOrder,
					createdDate = bullet.createdDate,
					lastModifiedDate = bullet.lastModifiedDate
				)
			}
		)
	}
}
