package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationResume
import org.springframework.stereotype.Component

@Component
class ApplicationResumeMapper : GenericMapper<ApplicationResumeDTO, ApplicationResume> {
	override fun createEntity(dto: ApplicationResumeDTO): ApplicationResume {
		return ApplicationResume(
			id = null,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			application = dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun updateEntity(dto: ApplicationResumeDTO, entity: ApplicationResume): ApplicationResume {
		return ApplicationResume(
			id = entity.id,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}

	override fun toDto(entity: ApplicationResume): ApplicationResumeDTO {
		return ApplicationResumeDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			applicationId = entity.application?.id,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}
}

