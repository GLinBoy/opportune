package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.service.InterviewAttachmentService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/interview-attachments")
class InterviewAttachmentResource(interviewAttachmentService: InterviewAttachmentService):
    GenericResource<UUID, InterviewAttachmentDTO, InterviewAttachmentService>(interviewAttachmentService) {
}

