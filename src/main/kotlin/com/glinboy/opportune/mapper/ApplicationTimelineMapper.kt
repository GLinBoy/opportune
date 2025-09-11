package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationTimelineDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationTimeline
import org.springframework.stereotype.Component
import java.util.*

@Component
class ApplicationTimelineMapper : GenericMapper<ApplicationTimelineDTO, ApplicationTimeline> {
	override fun createEntity(dto: ApplicationTimelineDTO): ApplicationTimeline {
		return ApplicationTimeline(
			id = UUID.randomUUID(),
			title = dto.title,
			description = dto.description,
			status = dto.status,
			occurredAt = dto.occurredAt,
			application = dto.applicationId?.let { Application(id = it) }
		)
	}

	override fun updateEntity(dto: ApplicationTimelineDTO, entity: ApplicationTimeline): ApplicationTimeline {
		return entity.copy(
			id = dto.id ?: entity.id,
			title = dto.title,
			description = dto.description,
			status = dto.status,
			occurredAt = dto.occurredAt,
			application = entity.application ?: dto.applicationId?.let { Application(id = it) }
		)
	}

	override fun toDto(entity: ApplicationTimeline): ApplicationTimelineDTO {
		return ApplicationTimelineDTO(
			id = entity.id,
			title = entity.title,
			description = entity.description,
			status = entity.status,
			occurredAt = entity.occurredAt,
			applicationId = entity.application?.id
		)
	}
}

