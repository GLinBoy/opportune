package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.entity.ApplicationMetaData
import com.glinboy.opportune.mapper.ApplicationMetaDataMapper
import com.glinboy.opportune.repository.ApplicationMetaDataRepository
import com.glinboy.opportune.service.ApplicationMetaDataService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationMetaDataServiceImpl(
	applicationMetaDataRepository: ApplicationMetaDataRepository,
	mapper: ApplicationMetaDataMapper
) :
	GenericServiceImpl<UUID, ApplicationMetaDataDTO, ApplicationMetaData, ApplicationMetaDataRepository, ApplicationMetaDataMapper>(
		applicationMetaDataRepository, mapper
	), ApplicationMetaDataService {
}
