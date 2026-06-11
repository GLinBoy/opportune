package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeAwardDTO
import com.glinboy.opportune.entity.ResumeAward
import com.glinboy.opportune.mapper.ResumeAwardMapper
import com.glinboy.opportune.repository.ResumeAwardRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ResumeAwardService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ResumeAwardServiceImpl(
	resumeAwardRepository: ResumeAwardRepository,
	mapper: ResumeAwardMapper
) : GenericServiceImpl<UUID, ResumeAward, ResumeAwardDTO, ResumeAwardRepository,
		ResumeAwardMapper>(resumeAwardRepository, mapper), ResumeAwardService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ResumeAward>("profile").get("id") }

	override fun validateOwnership(dto: ResumeAwardDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Resume Award for another user"
			)
		}
	}
}
