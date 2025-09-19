package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.InterviewNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InterviewNoteRepository : JpaRepository<InterviewNote, UUID>, JpaSpecificationExecutor<InterviewNote> {
	fun findAllByApplicationId(applicationId: UUID, pageable: Pageable): Page<InterviewNote>
	fun findOneByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<InterviewNote>

	@Modifying
	fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID): Unit
}
