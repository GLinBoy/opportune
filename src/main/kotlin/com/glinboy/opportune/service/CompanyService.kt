package com.glinboy.opportune.service

import com.glinboy.opportune.service.dto.CompanyDTO
import org.springframework.stereotype.Service

@Service
interface CompanyService {

	fun createCompany(companyDTO: CompanyDTO): CompanyDTO

	fun getCompany(id: Long): CompanyDTO

	fun updateCompany(id: Long, companyDTO: CompanyDTO): CompanyDTO

	fun deleteCompany(id: Long): Unit
}
