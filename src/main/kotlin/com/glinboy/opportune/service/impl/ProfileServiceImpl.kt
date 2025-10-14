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
	GenericServiceImpl<UUID, Profile, ProfileDTO, ProfileRepository,
		ProfileMapper>(profileRepository, mapper), ProfileService {

	private val currentUserID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")

	override fun getCurrentProfile(): Optional<ProfileDTO> {
		// FIXME user ID must read from the security context
		return repository.findById(currentUserID).map(mapper::toDto)
	}
}

