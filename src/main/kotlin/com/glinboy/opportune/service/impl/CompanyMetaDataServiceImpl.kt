package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.entity.CompanyMetaData
import com.glinboy.opportune.mapper.CompanyMetaDataMapper
import com.glinboy.opportune.repository.CompanyMetaDataRepository
import com.glinboy.opportune.service.CompanyMetaDataService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CompanyMetaDataServiceImpl(companyMetaDataRepository: CompanyMetaDataRepository, mapper: CompanyMetaDataMapper) :
	GenericServiceImpl<UUID, CompanyMetaDataDTO, CompanyMetaData, CompanyMetaDataRepository,
		CompanyMetaDataMapper>(companyMetaDataRepository, mapper), CompanyMetaDataService {

	override fun findAll(companyId: UUID, pageable: Pageable): Page<CompanyMetaDataDTO> =
		repository.findAllByCompanyId(companyId, pageable)
			.let {
			return it.map(mapper::toDto)
		}

	override fun findById(companyId: UUID, id: UUID): Optional<CompanyMetaDataDTO> =
		repository.findOneByCompanyIdAndId(companyId, id)
			.let {
				return it.map(mapper::toDto)
			}

	@Transactional
	override fun delete(companyId: UUID, id: UUID) {
		repository.deleteByCompanyIdAndId(companyId, id)
	}

}

