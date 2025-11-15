package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.CompanyStatus
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CompanyMapper : GenericMapper<CompanyDTO, Company> {
	override fun createEntity(dto: CompanyDTO): Company {
		return Company(
			id = null,
			name = dto.name,
			industry = dto.industry,
			website = dto.website,
			companySize = dto.companySize,
			location = dto.location,
			foundedYear = dto.foundedYear,
			description = dto.description,
			note = dto.note,
			logo = dto.logo,
			status = CompanyStatus.INTERESTED,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
		)
	}

	override fun updateEntity(dto: CompanyDTO, entity: Company): Company {
		return entity.copy(
			id = entity.id,
			name = dto.name,
			industry = dto.industry,
			website = dto.website,
			companySize = dto.companySize,
			location = dto.location,
			foundedYear = dto.foundedYear,
			description = dto.description,
			note = dto.note,
			logo = dto.logo,
			status = dto.status,
			applications = entity.applications,
			metaData = entity.metaData,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile,
		)
	}

	override fun toDto(entity: Company): CompanyDTO {
		return CompanyDTO(
			id = entity.id,
			name = entity.name,
			industry = entity.industry,
			website = entity.website,
			companySize = entity.companySize,
			location = entity.location,
			foundedYear = entity.foundedYear,
			description = entity.description,
			note = entity.note,
			logo = entity.logo,
			status = entity.status,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id,
		)
	}
}
