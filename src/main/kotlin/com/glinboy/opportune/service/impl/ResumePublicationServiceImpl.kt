package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumePublicationDTO
import com.glinboy.opportune.entity.ResumePublication
import com.glinboy.opportune.mapper.ResumePublicationMapper
import com.glinboy.opportune.repository.ResumePublicationRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ResumePublicationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ResumePublicationServiceImpl(
	resumePublicationRepository: ResumePublicationRepository,
	mapper: ResumePublicationMapper
) : GenericServiceImpl<UUID, ResumePublication, ResumePublicationDTO, ResumePublicationRepository,
		ResumePublicationMapper>(resumePublicationRepository, mapper), ResumePublicationService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ResumePublication>("profile").get("id") }

	override fun validateOwnership(dto: ResumePublicationDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Resume Publication for another user"
			)
		}
	}
}
