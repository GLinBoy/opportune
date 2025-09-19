package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationTimelineDTO
import com.glinboy.opportune.entity.ApplicationTimeline
import com.glinboy.opportune.mapper.ApplicationTimelineMapper
import com.glinboy.opportune.repository.ApplicationTimelineRepository
import com.glinboy.opportune.service.ApplicationTimelineService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationTimelineServiceImpl(
	applicationTimelineRepository: ApplicationTimelineRepository,
	mapper: ApplicationTimelineMapper
) : GenericServiceImpl<UUID, ApplicationTimelineDTO, ApplicationTimeline, ApplicationTimelineRepository,
	ApplicationTimelineMapper>(applicationTimelineRepository, mapper), ApplicationTimelineService {

	override fun findAll(applicationId: UUID, pageable: Pageable): Page<ApplicationTimelineDTO> =
		repository.findAllByApplicationId(applicationId, pageable)
			.let {
			return it.map(mapper::toDto)
		}

	override fun findById(applicationId: UUID, id: UUID): Optional<ApplicationTimelineDTO> =
		repository.findOneByApplicationIdAndId(applicationId, id)
			.let {
			return it.map(mapper::toDto)
		}

	@Transactional
	override fun delete(applicationId: UUID, id: UUID) {
		repository.deleteByApplicationIdAndId(applicationId, id)
	}

}
