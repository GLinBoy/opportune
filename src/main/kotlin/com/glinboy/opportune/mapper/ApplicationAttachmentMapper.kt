package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationAttachment
import org.springframework.stereotype.Component

@Component
class ApplicationAttachmentMapper : GenericMapper<ApplicationAttachmentDTO, ApplicationAttachment> {
	override fun createEntity(dto: ApplicationAttachmentDTO): ApplicationAttachment {
		return ApplicationAttachment(
			id = null,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			application = dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun updateEntity(dto: ApplicationAttachmentDTO, entity: ApplicationAttachment): ApplicationAttachment {
		return ApplicationAttachment(
			id = entity.id,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun toDto(entity: ApplicationAttachment): ApplicationAttachmentDTO {
		return ApplicationAttachmentDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			applicationId = entity.application?.id
		)
	}
}

