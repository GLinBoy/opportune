package com.glinboy.opportune.service

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface InterviewAttachmentService : GenericService<UUID, InterviewAttachmentDTO> {
	fun findByApplicationIdANdInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID): Optional<InterviewAttachmentDTO>
	fun findByApplicationIdAndInterviewNoteId(applicationId: UUID, interviewNoteId: UUID, pageable: Pageable): Page<InterviewAttachmentDTO>
	fun deleteByApplicationIdAndInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID)
	fun findByApplicationIdANdInterviewNoteIdAndIdForCurrentUser(applicationId: UUID, interviewNoteId: UUID, id: UUID): Optional<InterviewAttachmentDTO>
	fun findByApplicationIdAndInterviewNoteIdForCurrentUser(applicationId: UUID, interviewNoteId: UUID, pageable: Pageable): Page<InterviewAttachmentDTO>
	fun deleteByApplicationIdAndInterviewNoteIdAndIdForCurrentUser(applicationId: UUID, interviewNoteId: UUID, id: UUID): Unit
	fun upload(applicationId: UUID, interviewNoteId: UUID, file: MultipartFile): InterviewAttachmentDTO
	fun getFileResource(applicationId: UUID, interviewNoteId: UUID, id: UUID): Pair<Resource, InterviewAttachmentDTO>
}
