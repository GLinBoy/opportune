package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.service.ApplicationService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ApplicationServiceImpl( applicationRepository: ApplicationRepository, mapper: ModelMapper)
	: GenericServiceImpl<ApplicationDTO, Application, UUID, ApplicationRepository>(applicationRepository,
	mapper, ApplicationDTO::class.java, Application::class.java), ApplicationService {
}
