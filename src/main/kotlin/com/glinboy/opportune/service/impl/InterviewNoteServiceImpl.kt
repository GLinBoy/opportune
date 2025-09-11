package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewNoteDTO
import com.glinboy.opportune.entity.InterviewNote
import com.glinboy.opportune.mapper.InterviewNoteMapper
import com.glinboy.opportune.repository.InterviewNoteRepository
import com.glinboy.opportune.service.InterviewNoteService
import org.springframework.stereotype.Service
import java.util.*

@Service
class InterviewNoteServiceImpl(interviewNoteRepository: InterviewNoteRepository, mapper: InterviewNoteMapper) :
	GenericServiceImpl<UUID, InterviewNoteDTO, InterviewNote, InterviewNoteRepository,
		InterviewNoteMapper>(interviewNoteRepository, mapper), InterviewNoteService {
}

