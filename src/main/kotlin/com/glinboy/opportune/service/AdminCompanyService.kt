package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AdminCompanyListItemDTO
import com.glinboy.opportune.dto.AdminCompanyUpdateDTO
import com.glinboy.opportune.enums.CompanyStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface AdminCompanyService {
	fun listCompanies(filter: String?, pageable: Pageable): Page<AdminCompanyListItemDTO>
	fun updateStatus(id: UUID, status: CompanyStatus): AdminCompanyListItemDTO
	fun updateDetails(id: UUID, dto: AdminCompanyUpdateDTO): AdminCompanyListItemDTO
}
