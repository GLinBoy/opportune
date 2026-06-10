package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.SkillGroupDTO
import com.glinboy.opportune.entity.SkillGroup
import com.glinboy.opportune.mapper.SkillGroupMapper
import com.glinboy.opportune.repository.SkillGroupRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.SkillGroupService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class SkillGroupServiceImpl(
	skillGroupRepository: SkillGroupRepository,
	mapper: SkillGroupMapper
) : GenericServiceImpl<UUID, SkillGroup, SkillGroupDTO, SkillGroupRepository,
		SkillGroupMapper>(skillGroupRepository, mapper), SkillGroupService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<SkillGroup>("profile").get("id") }

	override fun validateOwnership(dto: SkillGroupDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Skill Group for another user"
			)
		}
	}
}
