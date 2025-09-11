package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.mapper.ApplicationMapper
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.service.ApplicationService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationServiceImpl(applicationRepository: ApplicationRepository, mapper: ApplicationMapper)
	: GenericServiceImpl<UUID, ApplicationDTO, Application, ApplicationRepository,
	ApplicationMapper>(applicationRepository, mapper), ApplicationService {
}
