package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.mapper.CompanyMapper
import com.glinboy.opportune.repository.CompanyRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CompanyServiceImpl(companyRepository: CompanyRepository, mapper: CompanyMapper)
	: GenericServiceImpl<CompanyDTO, Company, UUID, CompanyRepository, CompanyMapper>(companyRepository, mapper),
	CompanyService {
}
