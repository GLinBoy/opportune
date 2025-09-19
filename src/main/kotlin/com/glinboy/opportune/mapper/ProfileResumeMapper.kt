package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ProfileResume
import org.springframework.stereotype.Component

@Component
class ProfileResumeMapper : GenericMapper<ProfileResumeDTO, ProfileResume> {
	override fun createEntity(dto: ProfileResumeDTO): ProfileResume {
		return ProfileResume(
			id = null,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			profile = dto.profileId?.let { Profile(id = it) },
		)
	}

	override fun updateEntity(dto: ProfileResumeDTO, entity: ProfileResume): ProfileResume {
		return ProfileResume(
			id = entity.id,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			profile = entity.profile ?: dto.profileId?.let { Profile(id = it) },
		)
	}

	override fun toDto(entity: ProfileResume): ProfileResumeDTO {
		return ProfileResumeDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			profileId = entity.profile?.id
		)
	}
}

