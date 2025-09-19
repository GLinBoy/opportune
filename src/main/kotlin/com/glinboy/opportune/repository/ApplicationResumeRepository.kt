package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationResume
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationResumeRepository : JpaRepository<ApplicationResume, UUID>,
	JpaSpecificationExecutor<ApplicationResume> {

	fun findByApplicationId(applicationId: UUID): Optional<ApplicationResume>

	@Modifying
	fun deleteByApplicationId(applicationId: UUID): Unit
}
