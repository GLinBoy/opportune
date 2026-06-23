package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.SkillGroupDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.SkillGroup
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SkillGroupMapper : GenericMapper<SkillGroupDTO, SkillGroup> {
	override fun createEntity(dto: SkillGroupDTO): SkillGroup {
		return SkillGroup(
			id = null,
			category = dto.category,
			displayOrder = dto.displayOrder,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
			skills = dto.skills.distinct().toMutableList()
		)
	}

	override fun updateEntity(dto: SkillGroupDTO, entity: SkillGroup): SkillGroup {
		return entity.copy(
			id = entity.id,
			category = dto.category,
			displayOrder = dto.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			profile = entity.profile,
			skills = dto.skills.distinct().toMutableList()
		)
	}

	override fun toDto(entity: SkillGroup): SkillGroupDTO {
		return SkillGroupDTO(
			id = entity.id,
			category = entity.category,
			displayOrder = entity.displayOrder,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			profileId = entity.profile?.id,
			skills = entity.skills
		)
	}
}
