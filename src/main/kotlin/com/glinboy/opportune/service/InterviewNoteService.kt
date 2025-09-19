package com.glinboy.opportune.service

import com.glinboy.opportune.dto.InterviewNoteDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface InterviewNoteService : GenericService<UUID, InterviewNoteDTO> {
	fun findAll(applicationId: UUID, pageable: Pageable): Page<InterviewNoteDTO>
	fun findById(applicationId: UUID, id: UUID): Optional<InterviewNoteDTO>
	fun delete(applicationId: UUID, id: UUID): Unit
}
