package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeLanguageDTO
import com.glinboy.opportune.entity.ResumeLanguage
import com.glinboy.opportune.mapper.ResumeLanguageMapper
import com.glinboy.opportune.repository.ResumeLanguageRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ResumeLanguageService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ResumeLanguageServiceImpl(
	resumeLanguageRepository: ResumeLanguageRepository,
	mapper: ResumeLanguageMapper
) : GenericServiceImpl<UUID, ResumeLanguage, ResumeLanguageDTO, ResumeLanguageRepository,
		ResumeLanguageMapper>(resumeLanguageRepository, mapper), ResumeLanguageService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ResumeLanguage>("profile").get("id") }

	override fun validateOwnership(dto: ResumeLanguageDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Resume Language for another user"
			)
		}
	}
}
