package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.AdminCompanyListItemDTO
import com.glinboy.opportune.dto.AdminCompanyUpdateDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.enums.CompanyStatus
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.repository.CompanyRepository
import com.glinboy.opportune.service.AdminCompanyService
import io.github.perplexhub.rsql.RSQLJPASupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
@Transactional(readOnly = true)
class AdminCompanyServiceImpl(
	private val companyRepository: CompanyRepository,
	private val applicationRepository: ApplicationRepository,
) : AdminCompanyService {

	override fun listCompanies(filter: String?, pageable: Pageable): Page<AdminCompanyListItemDTO> {
		val page: Page<Company> = if (filter.isNullOrBlank()) {
			companyRepository.findAll(pageable)
		} else {
			val spec: Specification<Company> = RSQLJPASupport.toSpecification(filter)
			companyRepository.findAll(spec, pageable)
		}
		return page.map { company -> toListItem(company) }
	}

	@Transactional
	override fun updateStatus(id: UUID, status: CompanyStatus): AdminCompanyListItemDTO {
		val company = companyRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found") }
		val updated = companyRepository.save(company.copy(status = status, lastModifiedDate = Instant.now()))
		return toListItem(updated)
	}

	@Transactional
	override fun updateDetails(id: UUID, dto: AdminCompanyUpdateDTO): AdminCompanyListItemDTO {
		val company = companyRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found") }
		val updated = companyRepository.save(
			company.copy(
				name = dto.name ?: company.name,
				industry = dto.industry ?: company.industry,
				website = dto.website ?: company.website,
				companySize = dto.companySize ?: company.companySize,
				location = dto.location ?: company.location,
				foundedYear = dto.foundedYear ?: company.foundedYear,
				description = dto.description ?: company.description,
				note = dto.note ?: company.note,
				status = dto.status ?: company.status,
				lastModifiedDate = Instant.now(),
			)
		)
		return toListItem(updated)
	}

	private fun toListItem(company: Company): AdminCompanyListItemDTO {
		val appCount = company.id?.let { applicationRepository.countByCompany_Id(it) } ?: 0L
		return AdminCompanyListItemDTO(
			id = company.id,
			name = company.name,
			industry = company.industry,
			website = company.website,
			companySize = company.companySize,
			location = company.location,
			foundedYear = company.foundedYear,
			description = company.description,
			note = company.note,
			logo = company.logo,
			status = company.status,
			profileId = company.profile?.id,
			applicationCount = appCount,
			createdDate = company.createdDate,
			lastModifiedDate = company.lastModifiedDate,
		)
	}
}
