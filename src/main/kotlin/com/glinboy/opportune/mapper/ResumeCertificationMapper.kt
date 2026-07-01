package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ResumeCertificationDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ResumeCertification
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ResumeCertificationMapper : GenericMapper<ResumeCertificationDTO, ResumeCertification> {
	override fun createEntity(dto: ResumeCertificationDTO): ResumeCertification {
		return ResumeCertification(
			id = null,
			name = dto.name,
			issuingOrganization = dto.issuingOrganization,
			issueMonth = dto.issueMonth,
			issueYear = dto.issueYear,
			expirationMonth = dto.expirationMonth,
			expirationYear = dto.expirationYear,
			credentialId = dto.credentialId,
			credentialUrl = dto.credentialUrl,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: ResumeCertificationDTO, entity: ResumeCertification): ResumeCertification {
		return entity.copy(
			id = entity.id,
			name = dto.name,
			issuingOrganization = dto.issuingOrganization,
			issueMonth = dto.issueMonth,
			issueYear = dto.issueYear,
			expirationMonth = dto.expirationMonth,
			expirationYear = dto.expirationYear,
			credentialId = dto.credentialId,
			credentialUrl = dto.credentialUrl,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: ResumeCertification): ResumeCertificationDTO {
		return ResumeCertificationDTO(
			id = entity.id,
			name = entity.name,
			issuingOrganization = entity.issuingOrganization,
			issueMonth = entity.issueMonth,
			issueYear = entity.issueYear,
			expirationMonth = entity.expirationMonth,
			expirationYear = entity.expirationYear,
			credentialId = entity.credentialId,
			credentialUrl = entity.credentialUrl,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id
		)
	}
}
