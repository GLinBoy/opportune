package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.CompanyMetaData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyMetaDataRepository : JpaRepository<CompanyMetaData, UUID>, JpaSpecificationExecutor<CompanyMetaData> {
	fun findAllByCompanyId(companyId: UUID, pageable: Pageable): Page<CompanyMetaData>
	fun findOneByCompanyIdAndId(companyId: UUID, id: UUID): Optional<CompanyMetaData>

	@Modifying
	fun deleteByCompanyIdAndId(companyId: UUID, id: UUID): Unit
}

