package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.EducationDTO
import com.glinboy.opportune.entity.Education
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class EducationMapper : GenericMapper<EducationDTO, Education> {
	override fun createEntity(dto: EducationDTO): Education {
		return Education(
			id = null,
			school = dto.school,
			degree = dto.degree,
			fieldOfStudy = dto.fieldOfStudy,
			startYear = dto.startYear,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			gpa = dto.gpa,
			honors = dto.honors,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
			courses = dto.courses.toMutableList()
		)
	}

	override fun updateEntity(dto: EducationDTO, entity: Education): Education {
		return entity.copy(
			id = entity.id,
			school = dto.school,
			degree = dto.degree,
			fieldOfStudy = dto.fieldOfStudy,
			startYear = dto.startYear,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			gpa = dto.gpa,
			honors = dto.honors,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile,
			courses = dto.courses.toMutableList().toMutableList()
		)
	}

	override fun toDto(entity: Education): EducationDTO {
		return EducationDTO(
			id = entity.id,
			school = entity.school,
			degree = entity.degree,
			fieldOfStudy = entity.fieldOfStudy,
			startYear = entity.startYear,
			endYear = entity.endYear,
			isCurrent = entity.isCurrent,
			gpa = entity.gpa,
			honors = entity.honors,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id,
			courses = entity.courses
		)
	}
}
