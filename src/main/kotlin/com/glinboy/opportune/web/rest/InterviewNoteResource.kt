package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.InterviewNoteDTO
import com.glinboy.opportune.service.InterviewNoteService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/interview-notes")
class InterviewNoteResource(interviewNoteService: InterviewNoteService) :
	GenericResource<UUID, InterviewNoteDTO, InterviewNoteService>(interviewNoteService) {
}

