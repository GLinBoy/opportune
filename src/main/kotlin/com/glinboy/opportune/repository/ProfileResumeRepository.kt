package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ProfileResume
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileResumeRepository : JpaRepository<ProfileResume, UUID>, JpaSpecificationExecutor<ProfileResume> {
	fun findByProfileId(profileId: UUID): Optional<ProfileResume>

	@Modifying
	fun deleteByProfileId(profileId: UUID): Unit
}

