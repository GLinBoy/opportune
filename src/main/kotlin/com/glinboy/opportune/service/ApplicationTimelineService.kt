package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationTimelineDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ApplicationTimelineService : GenericService<UUID, ApplicationTimelineDTO> {
	fun findAll(applicationId: UUID, pageable: Pageable): Page<ApplicationTimelineDTO>
	fun findById(applicationId: UUID, id: UUID): Optional<ApplicationTimelineDTO>
	fun delete(applicationId: UUID, id: UUID): Unit
}
