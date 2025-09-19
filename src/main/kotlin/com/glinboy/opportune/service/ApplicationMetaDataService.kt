package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ApplicationMetaDataService : GenericService<UUID, ApplicationMetaDataDTO> {
	fun findAll(applicationId: UUID, pageable: Pageable): Page<ApplicationMetaDataDTO>
	fun findById(applicationId: UUID, id: UUID): Optional<ApplicationMetaDataDTO>
	fun delete(applicationId: UUID, id: UUID): Unit
}
