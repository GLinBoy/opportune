package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.entity.Application
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

	override fun getParentFieldName(): String = Application::class.java.simpleName.lowercase()

	override fun getParentId(dto: ApplicationAttachmentDTO): UUID? = dto.applicationId

	override fun getParentEntityName(): String = Application::class.java.simpleName
}
