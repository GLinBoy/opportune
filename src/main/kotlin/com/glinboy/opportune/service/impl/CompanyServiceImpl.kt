package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.repository.CompanyRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CompanyServiceImpl(companyRepository: CompanyRepository, mapper: ModelMapper)
	: GenericServiceImpl<CompanyDTO, Company, UUID, CompanyRepository>(companyRepository,
	mapper, CompanyDTO::class.java, Company::class.java), CompanyService {
}
