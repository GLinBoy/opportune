package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeCertificationDTO
import com.glinboy.opportune.entity.ResumeCertification
import com.glinboy.opportune.mapper.ResumeCertificationMapper
import com.glinboy.opportune.repository.ResumeCertificationRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ResumeCertificationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ResumeCertificationServiceImpl(
	resumeCertificationRepository: ResumeCertificationRepository,
	mapper: ResumeCertificationMapper
) : GenericServiceImpl<UUID, ResumeCertification, ResumeCertificationDTO, ResumeCertificationRepository,
		ResumeCertificationMapper>(resumeCertificationRepository, mapper), ResumeCertificationService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ResumeCertification>("profile").get("id") }

	override fun validateOwnership(dto: ResumeCertificationDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Resume Certification for another user"
			)
		}
	}
}
