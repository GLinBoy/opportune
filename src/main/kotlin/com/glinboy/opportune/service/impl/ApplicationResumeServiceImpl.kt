package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.ApplicationResume
import com.glinboy.opportune.mapper.ApplicationResumeMapper
import com.glinboy.opportune.repository.ApplicationResumeRepository
import com.glinboy.opportune.service.ApplicationResumeService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationResumeServiceImpl(applicationResumeRepository: ApplicationResumeRepository, mapper: ApplicationResumeMapper)
    : GenericServiceImpl<UUID, ApplicationResumeDTO, ApplicationResume, ApplicationResumeRepository,
    ApplicationResumeMapper>(applicationResumeRepository, mapper), ApplicationResumeService {
}

