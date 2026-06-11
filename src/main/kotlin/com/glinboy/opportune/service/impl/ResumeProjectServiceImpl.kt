package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeProjectDTO
import com.glinboy.opportune.entity.ResumeProject
import com.glinboy.opportune.mapper.ResumeProjectMapper
import com.glinboy.opportune.repository.ResumeProjectRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ResumeProjectService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ResumeProjectServiceImpl(
	resumeProjectRepository: ResumeProjectRepository,
	mapper: ResumeProjectMapper
) : GenericServiceImpl<UUID, ResumeProject, ResumeProjectDTO, ResumeProjectRepository,
		ResumeProjectMapper>(resumeProjectRepository, mapper), ResumeProjectService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<ResumeProject>("profile").get("id") }

	override fun validateOwnership(dto: ResumeProjectDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Resume Project for another user"
			)
		}
	}
}
