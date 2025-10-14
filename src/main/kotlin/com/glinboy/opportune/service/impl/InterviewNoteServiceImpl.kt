package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewNoteDTO
import com.glinboy.opportune.entity.InterviewNote
import com.glinboy.opportune.mapper.InterviewNoteMapper
import com.glinboy.opportune.repository.InterviewNoteRepository
import com.glinboy.opportune.service.InterviewNoteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class InterviewNoteServiceImpl(interviewNoteRepository: InterviewNoteRepository, mapper: InterviewNoteMapper) :
	GenericServiceImpl<UUID, InterviewNote, InterviewNoteDTO, InterviewNoteRepository,
		InterviewNoteMapper>(interviewNoteRepository, mapper), InterviewNoteService {

	override fun findAll(applicationId: UUID, pageable: Pageable): Page<InterviewNoteDTO> =
		repository.findAllByApplicationId(applicationId, pageable)
			.let {
			return it.map(mapper::toDto)
		}

	override fun findById(applicationId: UUID, id: UUID): Optional<InterviewNoteDTO> =
		repository.findOneByApplicationIdAndId(applicationId, id)
			.let {
			return it.map(mapper::toDto)
		}

	@Transactional
	override fun delete(applicationId: UUID, id: UUID) {
		repository.deleteByApplicationIdAndId(applicationId, id)
	}

}
