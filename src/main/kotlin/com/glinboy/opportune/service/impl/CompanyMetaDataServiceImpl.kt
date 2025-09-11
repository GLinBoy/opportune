package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.entity.CompanyMetaData
import com.glinboy.opportune.mapper.CompanyMetaDataMapper
import com.glinboy.opportune.repository.CompanyMetaDataRepository
import com.glinboy.opportune.service.CompanyMetaDataService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CompanyMetaDataServiceImpl(companyMetaDataRepository: CompanyMetaDataRepository, mapper: CompanyMetaDataMapper) :
	GenericServiceImpl<UUID, CompanyMetaDataDTO, CompanyMetaData, CompanyMetaDataRepository,
		CompanyMetaDataMapper>(companyMetaDataRepository, mapper), CompanyMetaDataService {
}

