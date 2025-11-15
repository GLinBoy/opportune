package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.CompanyMetaData
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CompanyMetaDataMapper : GenericMapper<CompanyMetaDataDTO, CompanyMetaData> {
	override fun createEntity(dto: CompanyMetaDataDTO): CompanyMetaData {
		return CompanyMetaData(
			id = null,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			company = dto.companyId?.let { Company(id = it) },
		)
	}

	override fun updateEntity(dto: CompanyMetaDataDTO, entity: CompanyMetaData): CompanyMetaData {
		return CompanyMetaData(
			id = entity.id,
			metaName = dto.metaName,
			metaValue = dto.metaValue,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			company = entity.company ?: dto.companyId?.let { Company(id = it) },
		)
	}

	override fun toDto(entity: CompanyMetaData): CompanyMetaDataDTO {
		return CompanyMetaDataDTO(
			id = entity.id,
			metaName = entity.metaName,
			metaValue = entity.metaValue,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			companyId = entity.company?.id,
		)
	}
}
