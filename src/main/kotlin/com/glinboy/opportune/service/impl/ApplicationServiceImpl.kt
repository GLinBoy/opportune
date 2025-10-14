package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.mapper.ApplicationDetailsMapper
import com.glinboy.opportune.mapper.ApplicationMapper
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.service.ApplicationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationServiceImpl(
	applicationRepository: ApplicationRepository,
	mapper: ApplicationMapper,
	private val applicationDetailsMapper: ApplicationDetailsMapper
) : GenericServiceImpl<UUID, Application, ApplicationDTO, ApplicationRepository,
	ApplicationMapper>(applicationRepository, mapper), ApplicationService {

	override fun getCompanyApplications(companyId: UUID, pageable: Pageable): Page<ApplicationDTO> =
		repository.findAllByCompanyId(companyId, pageable).let {
			return it.map(mapper::toDto)
		}

	override fun findAllApplications(pageable: Pageable): Page<ApplicationProjection> =
		repository.findAllApplications(pageable)

	override fun getApplicationDetails(id: UUID): Optional<ApplicationDetailsDTO> = repository.findApplicationDetailsById(id)
		.map { applicationDetailsMapper.toDto(it) }
}
