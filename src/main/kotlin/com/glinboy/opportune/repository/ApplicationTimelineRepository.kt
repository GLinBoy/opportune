package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationTimeline
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationTimelineRepository : JpaRepository<ApplicationTimeline, UUID>,
	JpaSpecificationExecutor<ApplicationTimeline> {
	fun findAllByApplicationId(applicationId: UUID, pageable: Pageable): Page<ApplicationTimeline>
	fun findOneByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<ApplicationTimeline>

	@Modifying
	fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID): Unit
}
