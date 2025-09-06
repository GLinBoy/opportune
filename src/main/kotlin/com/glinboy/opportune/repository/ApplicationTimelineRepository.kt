package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationTimeline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ApplicationTimelineRepository : JpaRepository<ApplicationTimeline, UUID>

