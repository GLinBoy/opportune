package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ResumeAwardDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ResumeAward
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ResumeAwardMapper : GenericMapper<ResumeAwardDTO, ResumeAward> {
	override fun createEntity(dto: ResumeAwardDTO): ResumeAward {
		return ResumeAward(
			id = null,
			title = dto.title,
			issuer = dto.issuer,
			awardDate = dto.awardDate,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: ResumeAwardDTO, entity: ResumeAward): ResumeAward {
		return entity.copy(
			id = entity.id,
			title = dto.title,
			issuer = dto.issuer,
			awardDate = dto.awardDate,
			description = dto.description,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: ResumeAward): ResumeAwardDTO {
		return ResumeAwardDTO(
			id = entity.id,
			title = entity.title,
			issuer = entity.issuer,
			awardDate = entity.awardDate,
			description = entity.description,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id
		)
	}
}
