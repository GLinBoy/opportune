package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.entity.ApplicationAttachment
import com.glinboy.opportune.mapper.ApplicationAttachmentMapper
import com.glinboy.opportune.repository.ApplicationAttachmentRepository
import com.glinboy.opportune.service.ApplicationAttachmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationAttachmentServiceImpl(
	applicationAttachmentRepository: ApplicationAttachmentRepository,
	mapper: ApplicationAttachmentMapper
) : GenericServiceImpl<UUID, ApplicationAttachment, ApplicationAttachmentDTO, ApplicationAttachmentRepository,
	ApplicationAttachmentMapper>(applicationAttachmentRepository, mapper), ApplicationAttachmentService {

	override fun findByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<ApplicationAttachmentDTO> =
		repository.findByApplicationIdAndId(applicationId, id)
			.map(mapper::toDto)
			.let { return it }

	override fun findByApplicationId(applicationId: UUID, pageable: Pageable): Page<ApplicationAttachmentDTO> =
		repository.findAllByApplicationId(applicationId, pageable)
			.map(mapper::toDto)
			.let { return it }

	@Transactional
	override fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID) =
		repository.deleteByApplicationIdAndId(applicationId, id)


}
