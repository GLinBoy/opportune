package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.entity.ApplicationAttachment
import com.glinboy.opportune.mapper.ApplicationAttachmentMapper
import com.glinboy.opportune.repository.ApplicationAttachmentRepository
import com.glinboy.opportune.service.ApplicationAttachmentService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationAttachmentServiceImpl(
	applicationAttachmentRepository: ApplicationAttachmentRepository,
	mapper: ApplicationAttachmentMapper
) : GenericChildServiceImpl<UUID, ApplicationAttachment, ApplicationAttachmentDTO, ApplicationAttachmentRepository,
	ApplicationAttachmentMapper>(applicationAttachmentRepository, mapper), ApplicationAttachmentService {

	override fun getParentFieldName(): String = "application"
}
