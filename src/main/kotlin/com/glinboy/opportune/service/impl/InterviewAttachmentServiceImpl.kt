package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.mapper.InterviewAttachmentMapper
import com.glinboy.opportune.repository.InterviewAttachmentRepository
import com.glinboy.opportune.service.InterviewAttachmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class InterviewAttachmentServiceImpl(
	interviewAttachmentRepository: InterviewAttachmentRepository,
	mapper: InterviewAttachmentMapper
) : GenericServiceImpl<UUID, InterviewAttachmentDTO, InterviewAttachment, InterviewAttachmentRepository,
	InterviewAttachmentMapper>(interviewAttachmentRepository, mapper), InterviewAttachmentService {

	override fun findByApplicationIdANdInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID): Optional<InterviewAttachmentDTO> =
		repository.findByInterviewNoteIdAndId(interviewNoteId, id)
			.map(mapper::toDto)
			.let { return it }

	override fun findByApplicationIdAndInterviewNoteId(applicationId: UUID, interviewNoteId: UUID, pageable: Pageable): Page<InterviewAttachmentDTO> {
		repository.findAllByInterviewNoteId(interviewNoteId, pageable)
			.map(mapper::toDto)
			.let { return it }
	}

	@Transactional
	override fun deleteByApplicationIdAndInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID) =
		repository.deleteByInterviewNoteIdAndId(interviewNoteId, id)


}
