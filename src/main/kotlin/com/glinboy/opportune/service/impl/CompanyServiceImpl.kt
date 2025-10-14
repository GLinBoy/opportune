package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.mapper.CompanyMapper
import com.glinboy.opportune.repository.CompanyRepository
import com.glinboy.opportune.service.CompanyService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CompanyServiceImpl(companyRepository: CompanyRepository, mapper: CompanyMapper) :
	GenericServiceImpl<UUID, Company, CompanyDTO, CompanyRepository, CompanyMapper>(companyRepository, mapper),
	CompanyService {
}
