package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.WorkExperience
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WorkExperienceRepository : JpaRepository<WorkExperience, UUID>, JpaSpecificationExecutor<WorkExperience> {
	fun findAllByProfileIdOrderByDisplayOrderAsc(profileId: UUID): List<WorkExperience>
}
