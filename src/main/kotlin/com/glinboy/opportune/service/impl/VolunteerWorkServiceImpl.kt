package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.VolunteerWorkDTO
import com.glinboy.opportune.entity.VolunteerWork
import com.glinboy.opportune.mapper.VolunteerWorkMapper
import com.glinboy.opportune.repository.VolunteerWorkRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.VolunteerWorkService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class VolunteerWorkServiceImpl(
	volunteerWorkRepository: VolunteerWorkRepository,
	mapper: VolunteerWorkMapper
) : GenericServiceImpl<UUID, VolunteerWork, VolunteerWorkDTO, VolunteerWorkRepository,
		VolunteerWorkMapper>(volunteerWorkRepository, mapper), VolunteerWorkService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<VolunteerWork>("profile").get("id") }

	override fun validateOwnership(dto: VolunteerWorkDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Volunteer Work for another user"
			)
		}
	}
}
