package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationResume
import org.springframework.stereotype.Component
import java.time.Instant

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
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun toDto(entity: ApplicationResume): ApplicationResumeDTO {
		return ApplicationResumeDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			applicationId = entity.application?.id,
		)
	}
}

