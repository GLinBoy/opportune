package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.mapper.InterviewAttachmentMapper
import com.glinboy.opportune.repository.InterviewAttachmentRepository
import com.glinboy.opportune.service.InterviewAttachmentService
import org.springframework.stereotype.Service
import java.util.*

@Service
class InterviewAttachmentServiceImpl(interviewAttachmentRepository: InterviewAttachmentRepository, mapper: InterviewAttachmentMapper)
    : GenericServiceImpl<UUID, InterviewAttachmentDTO, InterviewAttachment, InterviewAttachmentRepository,
    InterviewAttachmentMapper>(interviewAttachmentRepository, mapper), InterviewAttachmentService {
}

