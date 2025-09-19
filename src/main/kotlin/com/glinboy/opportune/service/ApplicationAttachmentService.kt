package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ApplicationAttachmentService : GenericService<UUID, ApplicationAttachmentDTO> {
	fun findByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<ApplicationAttachmentDTO>
	fun findByApplicationId(applicationId: UUID, pageable: Pageable): Page<ApplicationAttachmentDTO>
	fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID): Unit
}
