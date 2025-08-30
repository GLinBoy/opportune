package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.service.dto.CompanyDTO
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl : CompanyService {

	override fun createCompany(companyDTO: CompanyDTO): CompanyDTO {
		return CompanyDTO(
			id = 1,
			name = companyDTO.name,
			address = companyDTO.address
		)
	}

	override fun getCompany(id: Long): CompanyDTO {
		return CompanyDTO(
			id = id,
			name = "Sample Company",
			address = "123 Sample St"
		)
	}

	override fun updateCompany(id: Long, companyDTO: CompanyDTO): CompanyDTO {
		return CompanyDTO(
			id = id,
			name = companyDTO.name,
			address = companyDTO.address
		)
	}

	override fun deleteCompany(id: Long) {
		// Implementation for deleting a company
	}
}
