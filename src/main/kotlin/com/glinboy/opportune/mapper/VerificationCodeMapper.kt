package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.VerificationCodeDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.VerificationCode
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class VerificationCodeMapper : GenericMapper<VerificationCodeDTO, VerificationCode> {
	override fun createEntity(dto: VerificationCodeDTO): VerificationCode {
		return VerificationCode(
			id = null,
			type = dto.type,
			profile = dto.profileId?.let { Profile(id = it) }
		)
	}

	override fun updateEntity(dto: VerificationCodeDTO, entity: VerificationCode): VerificationCode {
		return entity.copy(
			id = entity.id,
			type = dto.type,
			profile = entity.profile,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now()
		)
	}

	override fun toDto(entity: VerificationCode): VerificationCodeDTO {
		return VerificationCodeDTO(
			id = entity.id,
			type = entity.type,
			profileId = entity.profile?.id,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}
}
