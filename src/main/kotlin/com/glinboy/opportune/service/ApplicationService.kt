package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ApplicationService : GenericService<UUID, ApplicationDTO> {
	fun getCompanyApplications(companyId: UUID, pageable: Pageable): Page<ApplicationDTO>
}
