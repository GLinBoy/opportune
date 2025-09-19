package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.InterviewNoteDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.InterviewNote
import org.springframework.stereotype.Component

@Component
class InterviewNoteMapper : GenericMapper<InterviewNoteDTO, InterviewNote> {
	override fun createEntity(dto: InterviewNoteDTO): InterviewNote {
		return InterviewNote(
			id = null,
			date = dto.date,
			notes = dto.notes,
			application = dto.applicationId?.let { Application(id = it) }
		)
	}

	override fun updateEntity(dto: InterviewNoteDTO, entity: InterviewNote): InterviewNote {
		return entity.copy(
			id = entity.id,
			date = dto.date,
			notes = dto.notes,
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
			attachments = entity.attachments
		)
	}

	override fun toDto(entity: InterviewNote): InterviewNoteDTO {
		return InterviewNoteDTO(
			id = entity.id,
			date = entity.date,
			notes = entity.notes,
			applicationId = entity.application?.id
		)
	}
}

