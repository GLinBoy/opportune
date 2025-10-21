package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationMetaData
import org.springframework.stereotype.Component

@Component
class ApplicationMetaDataMapper : GenericMapper<ApplicationMetaDataDTO, ApplicationMetaData> {
	override fun createEntity(dto: ApplicationMetaDataDTO): ApplicationMetaData {
		return ApplicationMetaData(
			id = null,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			application = dto.applicationId?.let { Application(id = it) }
		)
	}

	override fun updateEntity(dto: ApplicationMetaDataDTO, entity: ApplicationMetaData): ApplicationMetaData {
		return ApplicationMetaData(
			id = entity.id,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}

	override fun toDto(entity: ApplicationMetaData): ApplicationMetaDataDTO {
		return ApplicationMetaDataDTO(
			id = entity.id,
			metaName = entity.metaName,
			metaValue = entity.metaValue,
			applicationId = entity.application?.id,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate
		)
	}
}
