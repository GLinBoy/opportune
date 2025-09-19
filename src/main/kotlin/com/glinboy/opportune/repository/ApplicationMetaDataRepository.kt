package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationMetaData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationMetaDataRepository : JpaRepository<ApplicationMetaData, UUID>,
	JpaSpecificationExecutor<ApplicationMetaData> {
	fun findAllByApplicationId(applicationId: UUID, pageable: Pageable): Page<ApplicationMetaData>
	fun findOneByApplicationIdAndId(applicationId: UUID, id: UUID): Optional<ApplicationMetaData>

	@Modifying
	fun deleteByApplicationIdAndId(applicationId: UUID, id: UUID): Unit
}
