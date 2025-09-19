package com.glinboy.opportune.service

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface CompanyMetaDataService : GenericService<UUID, CompanyMetaDataDTO> {
	fun findAll(companyId: UUID, pageable: Pageable): Page<CompanyMetaDataDTO>
	fun findById(companyId: UUID, id: UUID): Optional<CompanyMetaDataDTO>
	fun delete(companyId: UUID, id: UUID): Unit
}

