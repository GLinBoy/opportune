package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.CompanyMetaData
import org.springframework.stereotype.Component
import java.util.*

@Component
class CompanyMetaDataMapper : GenericMapper<CompanyMetaDataDTO, CompanyMetaData> {
    override fun createEntity(dto: CompanyMetaDataDTO): CompanyMetaData {
        return CompanyMetaData(
            id = UUID.randomUUID(),
            metaName = dto.metaName,
            metaValue = dto.metaValue,
            company = dto.companyId?.let { Company(id = it) }
        )
    }

    override fun updateEntity(dto: CompanyMetaDataDTO, entity: CompanyMetaData): CompanyMetaData {
        return CompanyMetaData(
            id = dto.id ?: entity.id,
            metaName = dto.metaName,
            metaValue = dto.metaValue,
            company = entity.company ?: dto.companyId?.let { Company(id = it) }
        )
    }

    override fun toDto(entity: CompanyMetaData): CompanyMetaDataDTO {
        return CompanyMetaDataDTO(
            id = entity.id,
            metaName = entity.metaName,
            metaValue = entity.metaValue,
            companyId = entity.company?.id
        )
    }
}
