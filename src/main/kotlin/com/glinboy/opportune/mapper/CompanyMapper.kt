package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import org.springframework.stereotype.Component
import java.util.*

@Component
class CompanyMapper: GenericMapper<CompanyDTO, Company> {
    override fun toEntity(dto: CompanyDTO): Company {
        return Company(
            id = dto.id ?: UUID.randomUUID(),
            name = dto.name,
            industry = dto.industry,
            website = dto.website,
            companySize = dto.companySize,
            location = dto.location,
            foundedYear = dto.foundedYear,
            description = dto.description,
            note = dto.note,
            logo = dto.logo,
            status = dto.status
        )
    }

    override fun toDto(entity: Company): CompanyDTO {
        return CompanyDTO(
            id = entity.id,
            name = entity.name,
            industry = entity.industry,
            website = entity.website,
            companySize = entity.companySize,
            location = entity.location,
            foundedYear = entity.foundedYear,
            description = entity.description,
            note = entity.note,
            logo = entity.logo,
            status = entity.status
        )
    }
}
