package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.entity.InterviewNote
import org.springframework.stereotype.Component
import java.util.*

@Component
class InterviewAttachmentMapper : GenericMapper<InterviewAttachmentDTO, InterviewAttachment> {
	override fun createEntity(dto: InterviewAttachmentDTO): InterviewAttachment {
		return InterviewAttachment(
			interviewNote = dto.interviewNoteId?.let { InterviewNote(id = it) },
			id = UUID.randomUUID(),
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength
		)
	}

	override fun updateEntity(dto: InterviewAttachmentDTO, entity: InterviewAttachment): InterviewAttachment {
		return InterviewAttachment(
			interviewNote = entity.interviewNote ?: dto.interviewNoteId?.let { InterviewNote(id = it) },
			id = dto.id ?: entity.id ?: UUID.randomUUID(),
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength
		)
	}

	override fun toDto(entity: InterviewAttachment): InterviewAttachmentDTO {
		return InterviewAttachmentDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			interviewNoteId = entity.interviewNote?.id
		)
	}
}

