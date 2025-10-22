package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.entity.InterviewNote
import org.springframework.stereotype.Component

@Component
class InterviewAttachmentMapper : GenericMapper<InterviewAttachmentDTO, InterviewAttachment> {
	override fun createEntity(dto: InterviewAttachmentDTO): InterviewAttachment {
		return InterviewAttachment(
			id = null,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			interviewNote = dto.interviewNoteId?.let { InterviewNote(id = it) },
		)
	}

	override fun updateEntity(dto: InterviewAttachmentDTO, entity: InterviewAttachment): InterviewAttachment {
		return InterviewAttachment(
			id = entity.id,
			name = dto.name,
			path = dto.path,
			contentType = dto.contentType,
			contentLength = dto.contentLength,
			interviewNote = entity.interviewNote ?: dto.interviewNoteId?.let { InterviewNote(id = it) },
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}

	override fun toDto(entity: InterviewAttachment): InterviewAttachmentDTO {
		return InterviewAttachmentDTO(
			id = entity.id,
			name = entity.name,
			path = entity.path,
			contentType = entity.contentType,
			contentLength = entity.contentLength,
			interviewNoteId = entity.interviewNote?.id,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}
}

