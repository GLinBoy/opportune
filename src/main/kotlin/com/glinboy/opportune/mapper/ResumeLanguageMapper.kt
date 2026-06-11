package com.glinboy.opportune.mapper

import com.glinboy.opportune.enums.LanguageProficiency
import com.glinboy.opportune.dto.ResumeLanguageDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ResumeLanguage
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ResumeLanguageMapper : GenericMapper<ResumeLanguageDTO, ResumeLanguage> {
	override fun createEntity(dto: ResumeLanguageDTO): ResumeLanguage {
		return ResumeLanguage(
			id = null,
			language = dto.language,
			proficiency = dto.proficiency,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID())
		)
	}

	override fun updateEntity(dto: ResumeLanguageDTO, entity: ResumeLanguage): ResumeLanguage {
		return entity.copy(
			id = entity.id,
			language = dto.language,
			proficiency = dto.proficiency,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile
		)
	}

	override fun toDto(entity: ResumeLanguage): ResumeLanguageDTO {
		return ResumeLanguageDTO(
			id = entity.id,
			language = entity.language,
			proficiency = entity.proficiency,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id
		)
	}
}
