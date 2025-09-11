package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.entity.ProfileResume
import com.glinboy.opportune.mapper.ProfileResumeMapper
import com.glinboy.opportune.repository.ProfileResumeRepository
import com.glinboy.opportune.service.ProfileResumeService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileResumeServiceImpl(profileResumeRepository: ProfileResumeRepository, mapper: ProfileResumeMapper)
    : GenericServiceImpl<UUID, ProfileResumeDTO, ProfileResume, ProfileResumeRepository,
    ProfileResumeMapper>(profileResumeRepository, mapper), ProfileResumeService {
}

