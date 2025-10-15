package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationTimelineDTO
import com.glinboy.opportune.entity.ApplicationTimeline
import com.glinboy.opportune.mapper.ApplicationTimelineMapper
import com.glinboy.opportune.repository.ApplicationTimelineRepository
import com.glinboy.opportune.service.ApplicationTimelineService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationTimelineServiceImpl(
	applicationTimelineRepository: ApplicationTimelineRepository,
	mapper: ApplicationTimelineMapper
) : GenericChildServiceImpl<UUID, ApplicationTimeline, ApplicationTimelineDTO, ApplicationTimelineRepository,
	ApplicationTimelineMapper>(applicationTimelineRepository, mapper), ApplicationTimelineService {

	override fun getParentFieldName(): String = "application"
}
