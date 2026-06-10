package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.EducationDTO
import com.glinboy.opportune.entity.Education
import com.glinboy.opportune.mapper.EducationMapper
import com.glinboy.opportune.repository.EducationRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.EducationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class EducationServiceImpl(
	educationRepository: EducationRepository,
	mapper: EducationMapper
) : GenericServiceImpl<UUID, Education, EducationDTO, EducationRepository,
		EducationMapper>(educationRepository, mapper), EducationService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<Education>("profile").get("id") }

	override fun validateOwnership(dto: EducationDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Education for another user"
			)
		}
	}
}
