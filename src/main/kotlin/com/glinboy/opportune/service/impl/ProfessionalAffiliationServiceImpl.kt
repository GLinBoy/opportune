package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfessionalAffiliationDTO
import com.glinboy.opportune.entity.ProfessionalAffiliation
import com.glinboy.opportune.mapper.ProfessionalAffiliationMapper
import com.glinboy.opportune.repository.ProfessionalAffiliationRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ProfessionalAffiliationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ProfessionalAffiliationServiceImpl(
	professionalAffiliationRepository: ProfessionalAffiliationRepository,
	mapper: ProfessionalAffiliationMapper
) : GenericServiceImpl<UUID, ProfessionalAffiliation, ProfessionalAffiliationDTO, ProfessionalAffiliationRepository,
		ProfessionalAffiliationMapper>(professionalAffiliationRepository, mapper), ProfessionalAffiliationService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ProfessionalAffiliation>("profile").get("id") }

	override fun validateOwnership(dto: ProfessionalAffiliationDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Professional Affiliation for another user"
			)
		}
	}
}
