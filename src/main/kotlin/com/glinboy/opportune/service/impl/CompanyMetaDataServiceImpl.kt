package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.CompanyMetaData
import com.glinboy.opportune.mapper.CompanyMetaDataMapper
import com.glinboy.opportune.repository.CompanyMetaDataRepository
import com.glinboy.opportune.service.CompanyMetaDataService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CompanyMetaDataServiceImpl(
	companyMetaDataRepository: CompanyMetaDataRepository,
	mapper: CompanyMetaDataMapper
) :
	GenericChildServiceImpl<UUID, CompanyMetaData, CompanyMetaDataDTO, CompanyMetaDataRepository,
		CompanyMetaDataMapper>(companyMetaDataRepository, mapper), CompanyMetaDataService {

	override fun getParentFieldName(): String = Company::class.java.simpleName.lowercase()

	override fun getParentId(dto: CompanyMetaDataDTO): UUID? = dto.companyId

	override fun getParentEntityName(): String = Company::class.java.simpleName
}

