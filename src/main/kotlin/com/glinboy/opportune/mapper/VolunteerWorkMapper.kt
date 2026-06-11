package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.VolunteerWorkDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.VolunteerWork
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class VolunteerWorkMapper : GenericMapper<VolunteerWorkDTO, VolunteerWork> {
	override fun createEntity(dto: VolunteerWorkDTO): VolunteerWork {
		return VolunteerWork(
			id = null,
			role = dto.role,
			organization = dto.organization,
			location = dto.location,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: VolunteerWorkDTO, entity: VolunteerWork): VolunteerWork {
		return entity.copy(
			id = entity.id,
			role = dto.role,
			organization = dto.organization,
			location = dto.location,
			startMonth = dto.startMonth,
			startYear = dto.startYear,
			endMonth = dto.endMonth,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: VolunteerWork): VolunteerWorkDTO {
		return VolunteerWorkDTO(
			id = entity.id,
			role = entity.role,
			organization = entity.organization,
			location = entity.location,
			startMonth = entity.startMonth,
			startYear = entity.startYear,
			endMonth = entity.endMonth,
			endYear = entity.endYear,
			isCurrent = entity.isCurrent,
			description = entity.description,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id
		)
	}
}
