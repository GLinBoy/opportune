package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationTimeline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationTimelineRepository : JpaRepository<ApplicationTimeline, UUID>,
	JpaSpecificationExecutor<ApplicationTimeline>
