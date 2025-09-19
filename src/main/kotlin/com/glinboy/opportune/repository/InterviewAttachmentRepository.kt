package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.InterviewAttachment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InterviewAttachmentRepository : JpaRepository<InterviewAttachment, UUID>,
	JpaSpecificationExecutor<InterviewAttachment> {

	fun findByInterviewNoteIdAndId(interviewNoteId: UUID, id: UUID): Optional<InterviewAttachment>
	fun findAllByInterviewNoteId(interviewNoteId: UUID, pageable: Pageable): Page<InterviewAttachment>

	@Modifying
	fun deleteByInterviewNoteIdAndId(interviewNoteId: UUID, id: UUID): Unit
}
