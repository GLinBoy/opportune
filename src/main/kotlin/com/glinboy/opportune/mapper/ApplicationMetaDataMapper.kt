package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationMetaData
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ApplicationMetaDataMapper : GenericMapper<ApplicationMetaDataDTO, ApplicationMetaData> {
	override fun createEntity(dto: ApplicationMetaDataDTO): ApplicationMetaData {
		return ApplicationMetaData(
			id = null,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			application = dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun updateEntity(dto: ApplicationMetaDataDTO, entity: ApplicationMetaData): ApplicationMetaData {
		return ApplicationMetaData(
			id = entity.id,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			application = entity.application ?: dto.applicationId?.let { Application(id = it) },
		)
	}

	override fun toDto(entity: ApplicationMetaData): ApplicationMetaDataDTO {
		return ApplicationMetaDataDTO(
			id = entity.id,
			metaName = entity.metaName,
			metaValue = entity.metaValue,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			applicationId = entity.application?.id,
		)
	}
}
