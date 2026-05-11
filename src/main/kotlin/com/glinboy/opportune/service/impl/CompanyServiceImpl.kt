package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.mapper.CompanyMapper
import com.glinboy.opportune.repository.CompanyRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.CompanyService
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CompanyServiceImpl(companyRepository: CompanyRepository, mapper: CompanyMapper) :
	GenericServiceImpl<UUID, Company, CompanyDTO, CompanyRepository, CompanyMapper>(companyRepository, mapper),
	CompanyService {
	override fun currentUserSpecification(): Specification<Company> =
		createCurrentUserSpecification { it.get<UUID>("profile").get("id") }

	override fun validateOwnership(companyDTO: CompanyDTO) {
		if (companyDTO.profileId != null && companyDTO.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN,
				"You are not allowed to save or update company for another user")
		}
	}

	override fun findByNameInternal(name: String): Optional<CompanyDTO> {
		return repository.findByName(name)
			.map(mapper::toDto)
	}

	override fun findByNameForCurrentUser(name: String): Optional<CompanyDTO> {
		return repository.findByNameAndProfileId(name, SecurityUtils.getCurrentUserLoginID())
			.map(mapper::toDto)
	}
}
