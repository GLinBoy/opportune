package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.SkillGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SkillGroupRepository : JpaRepository<SkillGroup, UUID>, JpaSpecificationExecutor<SkillGroup> {
	fun findAllByProfileIdOrderByDisplayOrderAsc(profileId: UUID): List<SkillGroup>
}
