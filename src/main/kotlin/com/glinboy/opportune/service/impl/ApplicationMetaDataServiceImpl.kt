package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.entity.ApplicationMetaData
import com.glinboy.opportune.mapper.ApplicationMetaDataMapper
import com.glinboy.opportune.repository.ApplicationMetaDataRepository
import com.glinboy.opportune.service.ApplicationMetaDataService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationMetaDataServiceImpl(
	applicationMetaDataRepository: ApplicationMetaDataRepository,
	mapper: ApplicationMetaDataMapper
) :
	GenericServiceImpl<UUID, ApplicationMetaDataDTO, ApplicationMetaData, ApplicationMetaDataRepository, ApplicationMetaDataMapper>(
		applicationMetaDataRepository, mapper
	), ApplicationMetaDataService {

	override fun findAll(applicationId: UUID, pageable: Pageable): Page<ApplicationMetaDataDTO> =
		repository.findAllByApplicationId(applicationId, pageable)
			.let {
			return it.map(mapper::toDto)
		}

	override fun findById(applicationId: UUID, id: UUID): Optional<ApplicationMetaDataDTO> =
		repository.findOneByApplicationIdAndId(applicationId, id)
			.let {
			return it.map(mapper::toDto)
		}

	@Transactional
	override fun delete(applicationId: UUID, id: UUID) {
		repository.deleteByApplicationIdAndId(applicationId, id)
	}

}
