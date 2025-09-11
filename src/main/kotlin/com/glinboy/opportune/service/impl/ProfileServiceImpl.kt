package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.mapper.ProfileMapper
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.service.ProfileService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileServiceImpl(profileRepository: ProfileRepository, mapper: ProfileMapper) :
	GenericServiceImpl<UUID, ProfileDTO, Profile, ProfileRepository,
		ProfileMapper>(profileRepository, mapper), ProfileService {
}

