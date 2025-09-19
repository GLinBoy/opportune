package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationAttachment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationAttachmentRepository : JpaRepository<ApplicationAttachment, UUID>,
	JpaSpecificationExecutor<ApplicationAttachment> {

	fun findByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<ApplicationAttachment>
	fun findAllByApplicationId(applicationId: UUID, pageable: Pageable): Page<ApplicationAttachment>

	@Modifying
	fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID): Unit
}
