package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.ProfileResume
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ProfileMapper : GenericMapper<ProfileDTO, Profile> {
	override fun createEntity(dto: ProfileDTO): Profile {
		return Profile(
			id = null,
			email = dto.email,
			forename = dto.forename,
			surname = dto.surname,
			password = dto.password,
			jobTitle = dto.jobTitle,
			location = dto.location,
			avatar = dto.avatar,
			emailVerification = dto.emailVerification,
			lastLogin = dto.lastLogin,
			status = dto.status,
			subscription = dto.subscription,
			roles = emptySet(),
			resume = dto.resumeId?.let { ProfileResume(id = it) }
		)
	}

	override fun updateEntity(dto: ProfileDTO, entity: Profile): Profile {
		return entity.copy(
			id = entity.id,
			email = dto.email,
			forename = dto.forename,
			surname = dto.surname,
			password = dto.password,
			jobTitle = dto.jobTitle,
			location = dto.location,
			avatar = dto.avatar,
			emailVerification = dto.emailVerification,
			lastLogin = dto.lastLogin,
			status = dto.status,
			subscription = dto.subscription,
			roles = entity.roles,
			resume = entity.resume ?: dto.resumeId?.let { ProfileResume(id = it) },
			applications = entity.applications,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now()
		)
	}

	override fun toDto(entity: Profile): ProfileDTO {
		return ProfileDTO(
			id = entity.id,
			email = entity.email,
			forename = entity.forename,
			surname = entity.surname,
			jobTitle = entity.jobTitle,
			location = entity.location,
			avatar = entity.avatar,
			emailVerification = entity.emailVerification,
			lastLogin = entity.lastLogin,
			status = entity.status,
			subscription = entity.subscription,
			roles = entity.roles,
			resumeId = entity.resume?.id,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}
}

