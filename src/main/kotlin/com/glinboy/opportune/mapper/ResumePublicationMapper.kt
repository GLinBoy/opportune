package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ResumePublicationDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ResumePublication
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ResumePublicationMapper : GenericMapper<ResumePublicationDTO, ResumePublication> {
	override fun createEntity(dto: ResumePublicationDTO): ResumePublication {
		return ResumePublication(
			id = null,
			title = dto.title,
			publisher = dto.publisher,
			publicationDate = dto.publicationDate,
			url = dto.url,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: ResumePublicationDTO, entity: ResumePublication): ResumePublication {
		return entity.copy(
			id = entity.id,
			title = dto.title,
			publisher = dto.publisher,
			publicationDate = dto.publicationDate,
			url = dto.url,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: ResumePublication): ResumePublicationDTO {
		return ResumePublicationDTO(
			id = entity.id,
			title = entity.title,
			publisher = entity.publisher,
			publicationDate = entity.publicationDate,
			url = entity.url,
			description = entity.description,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id
		)
	}
}
