package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ProfessionalAffiliationDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ProfessionalAffiliation
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ProfessionalAffiliationMapper : GenericMapper<ProfessionalAffiliationDTO, ProfessionalAffiliation> {
	override fun createEntity(dto: ProfessionalAffiliationDTO): ProfessionalAffiliation {
		return ProfessionalAffiliation(
			id = null,
			organization = dto.organization,
			role = dto.role,
			startYear = dto.startYear,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: ProfessionalAffiliationDTO, entity: ProfessionalAffiliation): ProfessionalAffiliation {
		return entity.copy(
			id = entity.id,
			organization = dto.organization,
			role = dto.role,
			startYear = dto.startYear,
			endYear = dto.endYear,
			isCurrent = dto.isCurrent,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: ProfessionalAffiliation): ProfessionalAffiliationDTO {
		return ProfessionalAffiliationDTO(
			id = entity.id,
			organization = entity.organization,
			role = entity.role,
			startYear = entity.startYear,
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
